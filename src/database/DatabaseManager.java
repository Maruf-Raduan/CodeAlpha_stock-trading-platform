package database;

import model.*;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/trading.db";
    
    public DatabaseManager() {
        initDatabase();
    }
    
    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, balance REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, " +
                "symbol TEXT, type TEXT, quantity INTEGER, price REAL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS portfolio (" +
                "username TEXT, symbol TEXT, quantity INTEGER, avg_price REAL, " +
                "PRIMARY KEY (username, symbol))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS price_history (" +
                "symbol TEXT, price REAL, timestamp INTEGER)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS watchlist (" +
                "username TEXT, symbol TEXT, alert_price REAL, " +
                "PRIMARY KEY (username, symbol))");
                
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void saveUser(User user) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO users (username, balance) VALUES (?, ?)");
            ps.setString(1, user.getUsername());
            ps.setDouble(2, user.getBalance());
            ps.executeUpdate();
            
            savePortfolio(conn, user);
            saveTransactions(conn, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void savePortfolio(Connection conn, User user) throws SQLException {
        PreparedStatement del = conn.prepareStatement("DELETE FROM portfolio WHERE username = ?");
        del.setString(1, user.getUsername());
        del.executeUpdate();
        
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO portfolio (username, symbol, quantity, avg_price) VALUES (?, ?, ?, ?)");
        
        for (Map.Entry<String, Integer> entry : user.getPortfolio().getHoldings().entrySet()) {
            ps.setString(1, user.getUsername());
            ps.setString(2, entry.getKey());
            ps.setInt(3, entry.getValue());
            ps.setDouble(4, user.getPortfolio().getAvgPrice(entry.getKey()));
            ps.executeUpdate();
        }
    }
    
    private void saveTransactions(Connection conn, User user) throws SQLException {
        PreparedStatement check = conn.prepareStatement(
            "SELECT COUNT(*) FROM transactions WHERE username = ?");
        check.setString(1, user.getUsername());
        ResultSet rs = check.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) return;
        
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO transactions (username, symbol, type, quantity, price) VALUES (?, ?, ?, ?, ?)");
        
        for (Transaction t : user.getTransactionHistory()) {
            ps.setString(1, user.getUsername());
            ps.setString(2, t.getStockSymbol());
            ps.setString(3, t.getType());
            ps.setInt(4, t.getQuantity());
            ps.setDouble(5, t.getPricePerShare());
            ps.executeUpdate();
        }
    }
    
    public User loadUser(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User(username, rs.getDouble("balance"));
                loadPortfolio(conn, user);
                loadTransactions(conn, user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new User(username, 10000.0);
    }
    
    private void loadPortfolio(Connection conn, User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM portfolio WHERE username = ?");
        ps.setString(1, user.getUsername());
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            String symbol = rs.getString("symbol");
            int qty = rs.getInt("quantity");
            double avgPrice = rs.getDouble("avg_price");
            user.getPortfolio().addStock(symbol, qty, avgPrice);
        }
    }
    
    private void loadTransactions(Connection conn, User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM transactions WHERE username = ? ORDER BY timestamp");
        ps.setString(1, user.getUsername());
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            Transaction t = new Transaction(
                rs.getString("symbol"),
                rs.getString("type"),
                rs.getInt("quantity"),
                rs.getDouble("price")
            );
            user.addTransaction(t);
        }
    }
    
    public void savePriceHistory(String symbol, double price) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO price_history (symbol, price, timestamp) VALUES (?, ?, ?)");
            ps.setString(1, symbol);
            ps.setDouble(2, price);
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<PriceHistory.PricePoint> loadPriceHistory(String symbol, int limit) {
        List<PriceHistory.PricePoint> history = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM price_history WHERE symbol = ? ORDER BY timestamp DESC LIMIT ?");
            ps.setString(1, symbol);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                history.add(new PriceHistory.PricePoint(
                    rs.getDouble("price"),
                    rs.getLong("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(history);
        return history;
    }
}
