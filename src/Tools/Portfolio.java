package Tools;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private List<Stock> stocks;

    // Constructor
    // Automatically pulls AAPL, MSFT, and NVDA from Finnhub
    public Portfolio() throws Exception {

        stocks = new ArrayList<>();
        Stock apple =
                StockMarketTool.getStockValue("AAPL");
        Stock microsoft =
                StockMarketTool.getStockValue("MSFT");
        Stock nvidia =
                StockMarketTool.getStockValue("NVDA");
        stocks.add(apple);
        stocks.add(microsoft);
        stocks.add(nvidia);
    }


    // Adds a stock to the portfolio
    public void add(Stock stock) {
        stocks.add(stock);
    }


    // Removes a stock from the portfolio
    public void remove(Stock stock) {
        stocks.remove(stock);
    }


    // Finds the stock with the cheapest current price
    public Stock findCheapestStock() {
        if (stocks.isEmpty()) {
            return null;
        }
        Stock cheapestStock = stocks.get(0);
        for (Stock stock : stocks) {
            if (stock.getCurrentPrice()
                    < cheapestStock.getCurrentPrice()) {
                cheapestStock = stock;
            }
        }
        return cheapestStock;
    }

    // Finds the stock with the most expensive current price
    public Stock findMostExpensiveStock() {
        if (stocks.isEmpty()) {
            return null;
        }
        Stock mostExpensiveStock = stocks.get(0);
        for (Stock stock : stocks) {
            if (stock.getCurrentPrice()
                    > mostExpensiveStock.getCurrentPrice()) {
                mostExpensiveStock = stock;
            }
        }
        return mostExpensiveStock;
    }

    // Prints the average price of all stocks
    public void printAveragePriceOfAllStocks() {

        if (stocks.isEmpty()) {
            System.out.printf("%.2f%n", 0.0);
            return;
        }
        double totalPrice = 0;
        for (Stock stock : stocks) {
            totalPrice += stock.getCurrentPrice();
        }
        double averagePrice =
                totalPrice / stocks.size();
        System.out.printf("%.2f%n", averagePrice);
    }
}