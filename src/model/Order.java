package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    public enum OrderType { MARKET, LIMIT, STOP_LOSS }
    public enum OrderStatus { PENDING, EXECUTED, CANCELLED }
    
    private String orderId;
    private String symbol;
    private String action;
    private int quantity;
    private OrderType type;
    private double targetPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    
    public Order(String symbol, String action, int quantity, OrderType type, double targetPrice) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.type = type;
        this.targetPrice = targetPrice;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public String getAction() { return action; }
    public int getQuantity() { return quantity; }
    public OrderType getType() { return type; }
    public double getTargetPrice() { return targetPrice; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    @Override
    public String toString() {
        return String.format("%s %s %d %s @ $%.2f [%s]", 
            action, quantity, symbol, type, targetPrice, status);
    }
}
