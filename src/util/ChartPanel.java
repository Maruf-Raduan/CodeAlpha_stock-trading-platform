package util;

import model.PriceHistory;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartPanel extends JPanel {
    private List<PriceHistory.PricePoint> data;
    private String symbol;
    
    public ChartPanel(String symbol) {
        this.symbol = symbol;
        setPreferredSize(new Dimension(400, 250));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setData(List<PriceHistory.PricePoint> data) {
        this.data = data;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.drawString("No data available", getWidth()/2 - 50, getHeight()/2);
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = 40;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;
        
        double minPrice = data.stream().mapToDouble(p -> p.price).min().orElse(0);
        double maxPrice = data.stream().mapToDouble(p -> p.price).max().orElse(100);
        double priceRange = maxPrice - minPrice;
        if (priceRange == 0) priceRange = 1;
        
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 5; i++) {
            int y = padding + (height * i / 5);
            g2.drawLine(padding, y, padding + width, y);
            double price = maxPrice - (priceRange * i / 5);
            g2.drawString(String.format("$%.2f", price), 5, y + 5);
        }
        
        g2.setColor(new Color(0, 120, 215));
        g2.setStroke(new BasicStroke(2));
        
        for (int i = 0; i < data.size() - 1; i++) {
            int x1 = padding + (width * i / (data.size() - 1));
            int y1 = padding + height - (int)((data.get(i).price - minPrice) / priceRange * height);
            int x2 = padding + (width * (i + 1) / (data.size() - 1));
            int y2 = padding + height - (int)((data.get(i + 1).price - minPrice) / priceRange * height);
            g2.drawLine(x1, y1, x2, y2);
        }
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(symbol + " Price Chart", padding, 20);
    }
}
