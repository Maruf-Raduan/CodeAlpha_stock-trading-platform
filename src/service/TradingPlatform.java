package service;

import model.*;
import database.DatabaseManager;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;

public class TradingPlatform {
    private User user;
    private MarketData marketData;
    private DatabaseManager dbManager;
    private static final String DATA_FILE = "data/user_data.ser";
    
    public User getUser() { return user; }
    public MarketData getMarketData() { return marketData; }
    public DatabaseManager getDbManager() { return dbManager; }
    
    public TradingPlatform() {
        this.marketData = new MarketData();
        this.dbManager = new DatabaseManager();
        loadUser();
    }
    
    public boolean buyStock(String symbol, int quantity) {
        Stock stock = marketData.getStock(symbol);
        if (stock == null) {
            System.out.println("Stock not found!");
            return false;
        }
        
        double totalCost = stock.getCurrentPrice() * quantity;
        if (!user.deductBalance(totalCost)) {
            showMessage("Insufficient balance!");
            return false;
        }
        
        user.getPortfolio().addStock(symbol, quantity, stock.getCurrentPrice());
        user.addTransaction(new Transaction(symbol, "BUY", quantity, stock.getCurrentPrice()));
        showMessage(String.format("Bought %d shares of %s for $%.2f", quantity, symbol, totalCost));
        return true;
    }
    
    public boolean sellStock(String symbol, int quantity) {
        Stock stock = marketData.getStock(symbol);
        if (stock == null) {
            showMessage("Stock not found!");
            return false;
        }
        
        if (!user.getPortfolio().removeStock(symbol, quantity)) {
            showMessage("Insufficient shares to sell!");
            return false;
        }
        
        double totalRevenue = stock.getCurrentPrice() * quantity;
        user.addBalance(totalRevenue);
        user.addTransaction(new Transaction(symbol, "SELL", quantity, stock.getCurrentPrice()));
        showMessage(String.format("Sold %d shares of %s for $%.2f", quantity, symbol, totalRevenue));
        return true;
    }
    
    private void showMessage(String msg) {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.out.println(msg);
        } else {
            JOptionPane.showMessageDialog(null, msg);
        }
    }
    
    public void displayPortfolio() {
        System.out.println("\n=== YOUR PORTFOLIO ===");
        System.out.printf("Balance: $%.2f\n", user.getBalance());
        
        Portfolio portfolio = user.getPortfolio();
        if (portfolio.getHoldings().isEmpty()) {
            System.out.println("No holdings.");
            return;
        }
        
        System.out.println("\nHoldings:");
        for (var entry : portfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            int qty = entry.getValue();
            double avgPrice = portfolio.getAvgPrice(symbol);
            Stock stock = marketData.getStock(symbol);
            double currentPrice = stock.getCurrentPrice();
            double invested = avgPrice * qty;
            double currentValue = currentPrice * qty;
            double profitLoss = currentValue - invested;
            
            System.out.printf("%s: %d shares | Avg: $%.2f | Current: $%.2f | Value: $%.2f | P/L: $%.2f (%.2f%%)\n",
                symbol, qty, avgPrice, currentPrice, currentValue, profitLoss, (profitLoss/invested)*100);
        }
        
        double totalValue = portfolio.calculateTotalValue(marketData.getAllStocks());
        double totalPL = portfolio.calculateProfitLoss(marketData.getAllStocks());
        System.out.printf("\nTotal Portfolio Value: $%.2f\n", totalValue);
        System.out.printf("Total Profit/Loss: $%.2f\n", totalPL);
        System.out.printf("Net Worth: $%.2f\n", user.getBalance() + totalValue);
    }
    
    public void displayTransactionHistory() {
        System.out.println("\n=== TRANSACTION HISTORY ===");
        if (user.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : user.getTransactionHistory()) {
            System.out.println(t);
        }
    }
    
    public void updateMarket() {
        marketData.updatePrices();
        processOrders();
        checkAlerts();
    }
    
    private void processOrders() {
        for (Order order : user.getOrderBook().getPendingOrders()) {
            Stock stock = marketData.getStock(order.getSymbol());
            if (stock == null) continue;
            
            boolean execute = false;
            if (order.getType() == Order.OrderType.LIMIT) {
                if (order.getAction().equals("BUY") && stock.getCurrentPrice() <= order.getTargetPrice()) {
                    execute = true;
                } else if (order.getAction().equals("SELL") && stock.getCurrentPrice() >= order.getTargetPrice()) {
                    execute = true;
                }
            } else if (order.getType() == Order.OrderType.STOP_LOSS) {
                if (stock.getCurrentPrice() <= order.getTargetPrice()) {
                    execute = true;
                }
            }
            
            if (execute) {
                boolean success;
                if (order.getAction().equals("BUY")) {
                    success = executeOrderSilently(order.getSymbol(), order.getQuantity(), true);
                } else {
                    success = executeOrderSilently(order.getSymbol(), order.getQuantity(), false);
                }
                if (success) {
                    order.setStatus(Order.OrderStatus.EXECUTED);
                    showMessage("Order Executed: " + order.getType() + " " + order.getAction() + " " + 
                               order.getQuantity() + " " + order.getSymbol() + " @ $" + 
                               String.format("%.2f", order.getTargetPrice()));
                }
            }
        }
    }
    
    private boolean executeOrderSilently(String symbol, int quantity, boolean isBuy) {
        Stock stock = marketData.getStock(symbol);
        if (stock == null) return false;
        
        if (isBuy) {
            double totalCost = stock.getCurrentPrice() * quantity;
            if (!user.deductBalance(totalCost)) return false;
            user.getPortfolio().addStock(symbol, quantity, stock.getCurrentPrice());
            user.addTransaction(new Transaction(symbol, "BUY", quantity, stock.getCurrentPrice()));
        } else {
            if (!user.getPortfolio().removeStock(symbol, quantity)) return false;
            double totalRevenue = stock.getCurrentPrice() * quantity;
            user.addBalance(totalRevenue);
            user.addTransaction(new Transaction(symbol, "SELL", quantity, stock.getCurrentPrice()));
        }
        return true;
    }
    
    private void checkAlerts() {
        for (Map.Entry<String, Double> entry : user.getWatchlist().getAllAlerts().entrySet()) {
            Stock stock = marketData.getStock(entry.getKey());
            if (stock != null && Math.abs(stock.getCurrentPrice() - entry.getValue()) < 0.5) {
                showMessage("ALERT: " + entry.getKey() + " reached $" + String.format("%.2f", stock.getCurrentPrice()));
                user.getWatchlist().removeAlert(entry.getKey());
            }
        }
    }
    
    public void displayMarket() {
        marketData.displayMarket();
    }
    
    public void saveUser() {
        dbManager.saveUser(user);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    private void loadUser() {
        user = dbManager.loadUser("Trader");
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                User fileUser = (User) ois.readObject();
                if (fileUser.getTransactionHistory().size() > user.getTransactionHistory().size()) {
                    user = fileUser;
                }
                System.out.println("Welcome back, " + user.getUsername() + "!");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Loaded from database");
            }
        }
    }
}
