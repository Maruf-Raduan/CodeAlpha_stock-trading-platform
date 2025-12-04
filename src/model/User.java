package model;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private String username;
    private double balance;
    private Portfolio portfolio;
    private List<Transaction> transactionHistory;
    private Watchlist watchlist;
    private OrderBook orderBook;
    
    public User(String username, double initialBalance) {
        this.username = username;
        this.balance = initialBalance;
        this.portfolio = new Portfolio();
        this.transactionHistory = new ArrayList<>();
        this.watchlist = new Watchlist();
        this.orderBook = new OrderBook();
    }
    
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public Portfolio getPortfolio() { return portfolio; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    public Watchlist getWatchlist() { return watchlist; }
    public OrderBook getOrderBook() { return orderBook; }
    
    public void addBalance(double amount) {
        this.balance += amount;
    }
    
    public boolean deductBalance(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
}
