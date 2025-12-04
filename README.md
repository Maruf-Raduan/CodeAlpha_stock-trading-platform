# 📈 Stock Trading Platform

A professional Java-based stock trading simulation platform with modern GUI and OOP principles.

![Java](https://img.shields.io/badge/Java-SE-orange?style=flat&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=flat)
![SQLite](https://img.shields.io/badge/Database-SQLite-green?style=flat)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat)

## ✨ Features

- **Modern GUI Interface**: Professional Swing-based interface with real-time updates
- **Market Data Display**: Live simulated stock prices for 5 major stocks (AAPL, GOOGL, MSFT, AMZN, TSLA)
- **Buy/Sell Operations**: Quick trade execution with balance validation
- **Advanced Order Types**: Limit orders and stop-loss orders
- **Portfolio Tracking**: Real-time holdings, profit/loss, and performance metrics
- **Price Charts**: Visual price history for all stocks
- **Watchlist**: Track favorite stocks with price alerts
- **Transaction History**: Complete record of all trades with timestamps
- **Auto-Update**: Market prices refresh automatically every 10 seconds
- **Database Integration**: SQLite database for persistent data storage
- **Data Persistence**: Dual persistence with database and file serialization

## 📁 Project Structure

```
src/
├── Main.java                    # CLI entry point
├── gui/
│   ├── MainGUI.java            # GUI entry point
│   └── TradingGUI.java         # Main GUI interface
├── model/
│   ├── Stock.java              # Stock entity
│   ├── User.java               # User entity
│   ├── Portfolio.java          # Portfolio management
│   ├── Transaction.java        # Transaction records
│   ├── Order.java              # Order entity
│   ├── OrderBook.java          # Order management
│   ├── Watchlist.java          # Watchlist management
│   └── PriceHistory.java       # Price tracking
├── service/
│   ├── MarketData.java         # Market simulation
│   └── TradingPlatform.java    # Core trading logic
├── database/
│   └── DatabaseManager.java    # SQLite database operations
└── util/
    └── ChartPanel.java         # Price chart visualization
data/
├── user_data.ser               # Serialized user data
└── trading.db                  # SQLite database
lib/
└── sqlite-jdbc-3.45.0.0.jar   # SQLite JDBC driver
```

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- SQLite JDBC Driver (included in `lib/` folder)

### GUI Version (Recommended)
1. Clone the repository:
```bash
git clone https://github.com/Maruf-Raduan/CodeAlpha_stock-trading-platform.git
cd stock-trading-platform
```

2. Compile all Java files:
```bash
cd src
javac -cp ".;../lib/sqlite-jdbc-3.45.0.0.jar" gui/*.java model/*.java service/*.java database/*.java util/*.java
```

3. Run the GUI application:
```bash
java -cp ".;../lib/sqlite-jdbc-3.45.0.0.jar" gui.MainGUI
```

### CLI Version
1. Compile:
```bash
cd src
javac -cp ".;../lib/sqlite-jdbc-3.45.0.0.jar" Main.java model/*.java service/*.java database/*.java
```

2. Run:
```bash
java -cp ".;../lib/sqlite-jdbc-3.45.0.0.jar" Main
```

> **Note for Linux/Mac users**: Replace `;` with `:` in classpath (e.g., `".:../lib/sqlite-jdbc-3.45.0.0.jar"`)

## 💡 Usage

- Start with $10,000 initial balance
- **Trading Tab**: View live market data and execute trades
- **Watchlist Tab**: Add stocks to watchlist and set price alerts
- **Orders Tab**: Create limit orders and stop-loss orders
- **Charts Tab**: View price history charts for all stocks
- Monitor portfolio performance with real-time P/L calculations
- View recent transactions in the history panel
- Market prices auto-update every 10 seconds
- Orders execute automatically when target price is reached
- Data automatically saves to database on exit

## 🎯 OOP Concepts Used

- **Encapsulation**: Private fields with getters/setters
- **Abstraction**: Service layer separates business logic from presentation
- **Composition**: User has-a Portfolio, Portfolio has-a Map of holdings
- **Inheritance**: GUI components extend Swing classes
- **Polymorphism**: Custom table renderers and editors
- **Serialization**: File I/O for data persistence

## 🛠️ Technical Stack

- **Java SE**: Core programming language
- **Swing**: GUI framework for professional interface
- **SQLite**: Embedded database for data persistence
- **JDBC**: Database connectivity
- **Java I/O**: Serialization for backup persistence
- **MVC Pattern**: Separation of model, view, and controller logic

## 🚀 Advanced Features

- **Limit Orders**: Buy/sell at specific price points
- **Stop-Loss Orders**: Automatic sell when price drops
- **Price Alerts**: Get notified when stocks reach target prices
- **Price History**: Track and visualize price movements
- **Watchlist**: Monitor stocks without buying
- **Database Storage**: Reliable SQLite database backend



## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Maruf Raduan**
- GitHub: [@Maruf-Raduan](https://github.com/Maruf-Raduan)

## 🙏 Acknowledgments

- Built as part of Java internship project at CodeAlfa
- SQLite JDBC driver by Xerial
- Inspired by real-world trading platforms

## 📧 Contact

For questions or feedback, please reach out via email: raduanulhaque@gmail.com

---

⭐ Star this repository if you find it helpful!
