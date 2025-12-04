package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private String stockSymbol;
    private String type;
    private int quantity;
    private double pricePerShare;
    private LocalDateTime timestamp;
    
    public Transaction(String stockSymbol, String type, int quantity, double pricePerShare) {
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getStockSymbol() { return stockSymbol; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public double getTotalAmount() { return quantity * pricePerShare; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s %d shares of %s @ $%.2f (Total: $%.2f)",
            timestamp.format(formatter), type, quantity, stockSymbol, pricePerShare, getTotalAmount());
    }
}
