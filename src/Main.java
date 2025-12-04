import service.TradingPlatform;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TradingPlatform platform = new TradingPlatform();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== STOCK TRADING PLATFORM ===");
        
        while (true) {
            System.out.println("\n1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transaction History");
            System.out.println("6. Update Market Prices");
            System.out.println("7. Save & Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    platform.displayMarket();
                    break;
                case 2:
                    System.out.print("Enter stock symbol: ");
                    String buySymbol = scanner.nextLine().toUpperCase();
                    System.out.print("Enter quantity: ");
                    int buyQty = scanner.nextInt();
                    platform.buyStock(buySymbol, buyQty);
                    break;
                case 3:
                    System.out.print("Enter stock symbol: ");
                    String sellSymbol = scanner.nextLine().toUpperCase();
                    System.out.print("Enter quantity: ");
                    int sellQty = scanner.nextInt();
                    platform.sellStock(sellSymbol, sellQty);
                    break;
                case 4:
                    platform.displayPortfolio();
                    break;
                case 5:
                    platform.displayTransactionHistory();
                    break;
                case 6:
                    platform.updateMarket();
                    System.out.println("Market prices updated!");
                    break;
                case 7:
                    platform.saveUser();
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
