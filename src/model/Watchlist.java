package model;

import java.io.Serializable;
import java.util.*;

public class Watchlist implements Serializable {
    private Set<String> symbols;
    private Map<String, Double> priceAlerts;
    
    public Watchlist() {
        this.symbols = new HashSet<>();
        this.priceAlerts = new HashMap<>();
    }
    
    public void addSymbol(String symbol) { symbols.add(symbol); }
    public void removeSymbol(String symbol) { 
        symbols.remove(symbol);
        priceAlerts.remove(symbol);
    }
    public boolean contains(String symbol) { return symbols.contains(symbol); }
    public Set<String> getSymbols() { return new HashSet<>(symbols); }
    
    public void setAlert(String symbol, double price) { priceAlerts.put(symbol, price); }
    public void removeAlert(String symbol) { priceAlerts.remove(symbol); }
    public Double getAlert(String symbol) { return priceAlerts.get(symbol); }
    public Map<String, Double> getAllAlerts() { return new HashMap<>(priceAlerts); }
}
