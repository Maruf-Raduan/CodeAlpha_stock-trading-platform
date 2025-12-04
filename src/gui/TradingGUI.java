package gui;

import service.TradingPlatform;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Map;

public class TradingGUI extends JFrame {
    private TradingPlatform platform;
    private JTable marketTable, portfolioTable, transactionTable, watchlistTable, ordersTable;
    private JLabel balanceLabel, netWorthLabel, plLabel;
    private javax.swing.Timer autoUpdateTimer;
    private java.util.List<util.ChartPanel> chartPanels;
    
    public TradingGUI() {
        platform = new TradingPlatform();
        chartPanels = new java.util.ArrayList<>();
        initUI();
        startAutoUpdate();
    }
    
    private void initUI() {
        setTitle("Stock Trading Platform");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        updateAllData();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                platform.saveUser();
            }
        });
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Summary"));
        
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        balanceLabel = new JLabel("Balance: $0.00", SwingConstants.CENTER);
        netWorthLabel = new JLabel("Net Worth: $0.00", SwingConstants.CENTER);
        plLabel = new JLabel("P/L: $0.00", SwingConstants.CENTER);
        
        Font font = new Font("Arial", Font.BOLD, 16);
        balanceLabel.setFont(font);
        netWorthLabel.setFont(font);
        plLabel.setFont(font);
        
        infoPanel.add(balanceLabel);
        infoPanel.add(netWorthLabel);
        infoPanel.add(plLabel);
        panel.add(infoPanel);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel tradingPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tradingPanel.add(createMarketPanel());
        tradingPanel.add(createPortfolioPanel());
        
        tabbedPane.addTab("Trading", tradingPanel);
        tabbedPane.addTab("Watchlist", createWatchlistPanel());
        tabbedPane.addTab("Orders", createOrdersPanel());
        tabbedPane.addTab("Charts", createChartsPanel());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabbedPane);
        return panel;
    }
    
    private JPanel createMarketPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Market Data"));
        
        String[] columns = {"Symbol", "Name", "Price", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        
        marketTable = new JTable(model);
        marketTable.setRowHeight(30);
        marketTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        marketTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        
        JScrollPane scrollPane = new JScrollPane(marketTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("Refresh Prices");
        refreshBtn.addActionListener(e -> {
            platform.updateMarket();
            updateAllData();
        });
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPortfolioPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Portfolio"));
        
        String[] columns = {"Symbol", "Shares", "Avg Price", "Current", "Value", "P/L", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        
        portfolioTable = new JTable(model);
        portfolioTable.setRowHeight(30);
        portfolioTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        portfolioTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        
        panel.add(new JScrollPane(portfolioTable), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        
        String[] columns = {"Time", "Type", "Symbol", "Shares", "Price", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        transactionTable = new JTable(model);
        transactionTable.setRowHeight(25);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(0, 200));
        
        return panel;
    }
    
    private void updateAllData() {
        updateAccountInfo();
        updateMarketTable();
        updatePortfolioTable();
        updateTransactionTable();
        if (watchlistTable != null) updateWatchlistTable();
        if (ordersTable != null) updateOrdersTable();
        if (!chartPanels.isEmpty()) updateAllCharts();
    }
    
    private JPanel createWatchlistPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Symbol", "Price", "Alert", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        
        watchlistTable = new JTable(model);
        watchlistTable.setRowHeight(30);
        watchlistTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        watchlistTable.getColumn("Action").setCellEditor(new WatchlistButtonEditor(new JCheckBox(), this));
        
        panel.add(new JScrollPane(watchlistTable), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add to Watchlist");
        addBtn.addActionListener(e -> showAddWatchlistDialog());
        btnPanel.add(addBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Order ID", "Type", "Symbol", "Qty", "Target Price", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return col == 6; }
        };
        
        ordersTable = new JTable(model);
        ordersTable.setRowHeight(30);
        ordersTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        ordersTable.getColumn("Action").setCellEditor(new OrderButtonEditor(new JCheckBox(), this));
        
        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton limitBtn = new JButton("Limit Order");
        JButton stopBtn = new JButton("Stop-Loss Order");
        limitBtn.addActionListener(e -> showOrderDialog(Order.OrderType.LIMIT));
        stopBtn.addActionListener(e -> showOrderDialog(Order.OrderType.STOP_LOSS));
        btnPanel.add(limitBtn);
        btnPanel.add(stopBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (Stock stock : platform.getMarketData().getAllStocks().values()) {
            util.ChartPanel chart = new util.ChartPanel(stock.getSymbol());
            chartPanels.add(chart);
            updateChart(chart, stock.getSymbol());
            panel.add(chart);
        }
        
        return panel;
    }
    
    private void updateChart(util.ChartPanel chart, String symbol) {
        PriceHistory history = platform.getMarketData().getPriceHistory(symbol);
        if (history != null && !history.getHistory().isEmpty()) {
            chart.setData(history.getHistory());
        }
    }
    
    private void updateAllCharts() {
        for (util.ChartPanel chart : chartPanels) {
            PriceHistory history = platform.getMarketData().getPriceHistory(chart.getSymbol());
            if (history != null) {
                chart.setData(history.getHistory());
            }
        }
    }
    
    private void updateWatchlistTable() {
        DefaultTableModel model = (DefaultTableModel) watchlistTable.getModel();
        model.setRowCount(0);
        
        Watchlist watchlist = platform.getUser().getWatchlist();
        for (String symbol : watchlist.getSymbols()) {
            Stock stock = platform.getMarketData().getStock(symbol);
            if (stock != null) {
                Double alert = watchlist.getAlert(symbol);
                model.addRow(new Object[]{
                    symbol,
                    String.format("$%.2f", stock.getCurrentPrice()),
                    alert != null ? String.format("$%.2f", alert) : "None",
                    "Remove"
                });
            }
        }
    }
    
    private void updateOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
        model.setRowCount(0);
        
        for (Order order : platform.getUser().getOrderBook().getAllOrders()) {
            model.addRow(new Object[]{
                order.getOrderId(),
                order.getType(),
                order.getSymbol(),
                order.getQuantity(),
                String.format("$%.2f", order.getTargetPrice()),
                order.getStatus(),
                "Cancel"
            });
        }
    }
    
    private void showAddWatchlistDialog() {
        String[] symbols = platform.getMarketData().getAllStocks().keySet().toArray(new String[0]);
        String symbol = (String) JOptionPane.showInputDialog(this, "Select stock:", "Add to Watchlist",
            JOptionPane.QUESTION_MESSAGE, null, symbols, symbols[0]);
        
        if (symbol != null) {
            platform.getUser().getWatchlist().addSymbol(symbol);
            
            String priceStr = JOptionPane.showInputDialog(this, "Set price alert (optional):", "");
            if (priceStr != null && !priceStr.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    platform.getUser().getWatchlist().setAlert(symbol, price);
                } catch (NumberFormatException e) {}
            }
            updateWatchlistTable();
        }
    }
    
    private void showOrderDialog(Order.OrderType type) {
        String[] symbols = platform.getMarketData().getAllStocks().keySet().toArray(new String[0]);
        String symbol = (String) JOptionPane.showInputDialog(this, "Select stock:", "Create Order",
            JOptionPane.QUESTION_MESSAGE, null, symbols, symbols[0]);
        
        if (symbol == null) return;
        
        String[] actions = type == Order.OrderType.STOP_LOSS ? new String[]{"SELL"} : new String[]{"BUY", "SELL"};
        String action = (String) JOptionPane.showInputDialog(this, "Action:", "Create Order",
            JOptionPane.QUESTION_MESSAGE, null, actions, actions[0]);
        
        if (action == null) return;
        
        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:", "1");
        String priceStr = JOptionPane.showInputDialog(this, "Target Price:", "");
        
        if (qtyStr != null && priceStr != null) {
            try {
                int qty = Integer.parseInt(qtyStr);
                double price = Double.parseDouble(priceStr);
                Order order = new Order(symbol, action, qty, type, price);
                platform.getUser().getOrderBook().addOrder(order);
                updateOrdersTable();
                JOptionPane.showMessageDialog(this, "Order created successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input!");
            }
        }
    }
    
    private void updateAccountInfo() {
        User user = platform.getUser();
        double balance = user.getBalance();
        double portfolioValue = user.getPortfolio().calculateTotalValue(platform.getMarketData().getAllStocks());
        double pl = user.getPortfolio().calculateProfitLoss(platform.getMarketData().getAllStocks());
        double invested = portfolioValue - pl;
        
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
        netWorthLabel.setText(String.format("Net Worth: $%.2f", balance + portfolioValue));
        plLabel.setText(String.format("P/L: $%.2f (%.2f%%)", pl, invested > 0 ? (pl/invested)*100 : 0));
        plLabel.setForeground(pl >= 0 ? new Color(0, 150, 0) : Color.RED);
    }
    
    private void updateMarketTable() {
        DefaultTableModel model = (DefaultTableModel) marketTable.getModel();
        model.setRowCount(0);
        
        for (Stock stock : platform.getMarketData().getAllStocks().values()) {
            model.addRow(new Object[]{
                stock.getSymbol(),
                stock.getName(),
                String.format("$%.2f", stock.getCurrentPrice()),
                "Trade"
            });
        }
    }
    
    private void updatePortfolioTable() {
        DefaultTableModel model = (DefaultTableModel) portfolioTable.getModel();
        model.setRowCount(0);
        
        Portfolio portfolio = platform.getUser().getPortfolio();
        for (Map.Entry<String, Integer> entry : portfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            int qty = entry.getValue();
            double avgPrice = portfolio.getAvgPrice(symbol);
            Stock stock = platform.getMarketData().getStock(symbol);
            if (stock == null) continue;
            
            double currentPrice = stock.getCurrentPrice();
            double value = currentPrice * qty;
            double pl = (currentPrice - avgPrice) * qty;
            
            model.addRow(new Object[]{
                symbol,
                qty,
                String.format("$%.2f", avgPrice),
                String.format("$%.2f", currentPrice),
                String.format("$%.2f", value),
                String.format("$%.2f", pl),
                "Sell"
            });
        }
    }
    
    private void updateTransactionTable() {
        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        model.setRowCount(0);
        
        java.util.List<Transaction> transactions = platform.getUser().getTransactionHistory();
        int start = Math.max(0, transactions.size() - 10);
        for (int i = transactions.size() - 1; i >= start; i--) {
            Transaction t = transactions.get(i);
            model.addRow(new Object[]{
                new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()),
                t.getType(),
                t.getStockSymbol(),
                t.getQuantity(),
                String.format("$%.2f", t.getPricePerShare()),
                String.format("$%.2f", t.getTotalAmount())
            });
        }
    }
    
    private void showTradeDialog(String symbol, boolean isBuy) {
        Stock stock = platform.getMarketData().getStock(symbol);
        String action = isBuy ? "Buy" : "Sell";
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Stock:"));
        panel.add(new JLabel(stock.getSymbol() + " - $" + String.format("%.2f", stock.getCurrentPrice())));
        panel.add(new JLabel("Quantity:"));
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        panel.add(qtySpinner);
        panel.add(new JLabel("Total:"));
        JLabel totalLabel = new JLabel("$0.00");
        panel.add(totalLabel);
        
        qtySpinner.addChangeListener(e -> {
            int qty = (Integer) qtySpinner.getValue();
            double total = qty * stock.getCurrentPrice();
            totalLabel.setText(String.format("$%.2f", total));
        });
        qtySpinner.setValue(1);
        
        int result = JOptionPane.showConfirmDialog(this, panel, action + " " + symbol, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int qty = (Integer) qtySpinner.getValue();
            if (isBuy) {
                platform.buyStock(symbol, qty);
            } else {
                platform.sellStock(symbol, qty);
            }
            updateAllData();
        }
    }
    
    private void startAutoUpdate() {
        autoUpdateTimer = new javax.swing.Timer(10000, e -> {
            platform.updateMarket();
            updateAllData();
        });
        autoUpdateTimer.start();
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;
        private JTable table;
        
        public ButtonEditor(JCheckBox checkBox, TradingGUI parent) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                if (table == marketTable) {
                    String symbol = (String) table.getValueAt(row, 0);
                    parent.showTradeDialog(symbol, true);
                } else if (table == portfolioTable) {
                    String symbol = (String) table.getValueAt(row, 0);
                    parent.showTradeDialog(symbol, false);
                }
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.table = table;
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }
        
        public Object getCellEditorValue() {
            clicked = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
    
    class WatchlistButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;
        private JTable table;
        
        public WatchlistButtonEditor(JCheckBox checkBox, TradingGUI parent) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                String symbol = (String) watchlistTable.getValueAt(row, 0);
                platform.getUser().getWatchlist().removeSymbol(symbol);
                updateWatchlistTable();
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.table = table;
            this.row = row;
            button.setText(value != null ? value.toString() : "");
            return button;
        }
        
        public Object getCellEditorValue() { return button.getText(); }
    }
    
    class OrderButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;
        
        public OrderButtonEditor(JCheckBox checkBox, TradingGUI parent) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                String orderId = (String) ordersTable.getValueAt(row, 0);
                platform.getUser().getOrderBook().removeOrder(orderId);
                updateOrdersTable();
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            this.row = row;
            button.setText(value != null ? value.toString() : "");
            return button;
        }
        
        public Object getCellEditorValue() { return button.getText(); }
    }
}
