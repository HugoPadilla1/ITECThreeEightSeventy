package Tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class Investment {

    private final Portfolio portfolio;
    private final Map<String, Integer> shares;
    private final Map<String, Stock> stocks;

    public Investment() throws Exception {
        portfolio = new Portfolio();
        shares = new LinkedHashMap<>();
        stocks = new LinkedHashMap<>();

        // Remove the three default stocks from Portfolio.
        for (int i = 0; i < 3; i++) {
            portfolio.remove(portfolio.findCheapestStock());
        }
    }

    public void loadPortfolioFromWeb(String url) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(url).openStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                String symbol = fields[0].trim().toUpperCase();
                int numberOfShares = Integer.parseInt(fields[1].trim());

                // Use the existing Finnhub API method.
                Stock stock = StockMarketTool.getStockValue(symbol);

                portfolio.add(stock);
                shares.put(symbol, numberOfShares);
                stocks.put(symbol, stock);
            }
        }
    }

    public Stock findCheapestStock() {
        return portfolio.findCheapestStock();
    }

    public Stock findMostExpensiveStock() {
        return portfolio.findMostExpensiveStock();
    }

    public double getTotalValueOfInvestment() {
        double total = 0.0;

        for (Map.Entry<String, Integer> entry : shares.entrySet()) {
            Stock stock = stocks.get(entry.getKey());

            total += stock.getCurrentPrice() * entry.getValue();
        }

        return total;
    }
}
