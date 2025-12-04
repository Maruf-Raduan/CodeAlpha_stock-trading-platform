package model;

import java.io.Serializable;
import java.util.*;

public class PriceHistory implements Serializable {
    private String symbol;
    private List<PricePoint> history;
    
    public PriceHistory(String symbol) {
        this.symbol = symbol;
        this.history = new ArrayList<>();
    }
    
    public void addPrice(double price, long timestamp) {
        history.add(new PricePoint(price, timestamp));
        if (history.size() > 100) history.remove(0);
    }
    
    public List<PricePoint> getHistory() { return new ArrayList<>(history); }
    public String getSymbol() { return symbol; }
    
    public static class PricePoint implements Serializable {
        public final double price;
        public final long timestamp;
        
        public PricePoint(double price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }
}
