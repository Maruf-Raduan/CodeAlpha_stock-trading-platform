package service;

import model.*;
import java.util.*;
import database.DatabaseManager;

public class MarketData {
    private Map<String, Stock> stocks;
    private Map<String, PriceHistory> priceHistories;
    private Random random;
    private DatabaseManager dbManager;
    
    public MarketData() {
        this.stocks = new HashMap<>();
        this.priceHistories = new HashMap<>();
        this.random = new Random();
        this.dbManager = new DatabaseManager();
        initializeMarket();
    }
    
    private void initializeMarket() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.50));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 140.25));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 380.75));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 145.30));
        stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 245.60));
        
        for (String symbol : stocks.keySet()) {
            priceHistories.put(symbol, new PriceHistory(symbol));
        }
        
        initializePriceHistory();
    }
    
    private void initializePriceHistory() {
        long currentTime = System.currentTimeMillis();
        long interval = 10000;
        
        for (Stock stock : stocks.values()) {
            PriceHistory history = priceHistories.get(stock.getSymbol());
            double basePrice = stock.getCurrentPrice();
            
            for (int i = 30; i >= 0; i--) {
                double variation = (random.nextDouble() - 0.5) * 20;
                double price = Math.max(1, basePrice + variation);
                history.addPrice(price, currentTime - (i * interval));
            }
            
            stock.setCurrentPrice(basePrice);
        }
    }
    
    public void updatePrices() {
        long timestamp = System.currentTimeMillis();
        for (Stock stock : stocks.values()) {
            double change = (random.nextDouble() - 0.5) * 10;
            double newPrice = Math.max(1, stock.getCurrentPrice() + change);
            stock.setCurrentPrice(newPrice);
            
            PriceHistory history = priceHistories.get(stock.getSymbol());
            history.addPrice(newPrice, timestamp);
            dbManager.savePriceHistory(stock.getSymbol(), newPrice);
        }
    }
    
    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }
    
    public Map<String, Stock> getAllStocks() {
        return new HashMap<>(stocks);
    }
    
    public PriceHistory getPriceHistory(String symbol) {
        return priceHistories.get(symbol);
    }
    
    public void displayMarket() {
        System.out.println("\n=== MARKET DATA ===");
        for (Stock stock : stocks.values()) {
            System.out.println(stock);
        }
    }
}
