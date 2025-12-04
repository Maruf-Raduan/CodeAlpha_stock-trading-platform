package model;

import java.io.Serializable;
import java.util.*;

public class Portfolio implements Serializable {
    private Map<String, Integer> holdings;
    private Map<String, Double> avgPurchasePrice;
    
    public Portfolio() {
        this.holdings = new HashMap<>();
        this.avgPurchasePrice = new HashMap<>();
    }
    
    public void addStock(String symbol, int quantity, double price) {
        int currentQty = holdings.getOrDefault(symbol, 0);
        double currentAvg = avgPurchasePrice.getOrDefault(symbol, 0.0);
        
        double newAvg = ((currentAvg * currentQty) + (price * quantity)) / (currentQty + quantity);
        holdings.put(symbol, currentQty + quantity);
        avgPurchasePrice.put(symbol, newAvg);
    }
    
    public boolean removeStock(String symbol, int quantity) {
        int currentQty = holdings.getOrDefault(symbol, 0);
        if (currentQty < quantity) return false;
        
        if (currentQty == quantity) {
            holdings.remove(symbol);
            avgPurchasePrice.remove(symbol);
        } else {
            holdings.put(symbol, currentQty - quantity);
        }
        return true;
    }
    
    public int getQuantity(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }
    
    public double getAvgPrice(String symbol) {
        return avgPurchasePrice.getOrDefault(symbol, 0.0);
    }
    
    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }
    
    public double calculateTotalValue(Map<String, Stock> marketStocks) {
        double total = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = marketStocks.get(entry.getKey());
            if (stock != null) {
                total += stock.getCurrentPrice() * entry.getValue();
            }
        }
        return total;
    }
    
    public double calculateProfitLoss(Map<String, Stock> marketStocks) {
        double profitLoss = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = marketStocks.get(entry.getKey());
            if (stock != null) {
                double invested = avgPurchasePrice.get(entry.getKey()) * entry.getValue();
                double current = stock.getCurrentPrice() * entry.getValue();
                profitLoss += (current - invested);
            }
        }
        return profitLoss;
    }
}
