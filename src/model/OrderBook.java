package model;

import java.io.Serializable;
import java.util.*;

public class OrderBook implements Serializable {
    private List<Order> orders;
    
    public OrderBook() {
        this.orders = new ArrayList<>();
    }
    
    public void addOrder(Order order) {
        orders.add(order);
    }
    
    public void removeOrder(String orderId) {
        orders.removeIf(o -> o.getOrderId().equals(orderId));
    }
    
    public List<Order> getPendingOrders() {
        List<Order> pending = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() == Order.OrderStatus.PENDING) {
                pending.add(o);
            }
        }
        return pending;
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
