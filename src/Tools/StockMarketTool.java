package Tools;

import org.apache.commons.io.IOUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URL;

public class StockMarketTool {

    public static final String PUBLIC_KEY =
            "d9uvun1r01qv408ie970d9uvun1r01qv408ie97g";

    public static final String PROFILE_URL =
            "https://finnhub.io/api/v1/stock/profile2?symbol=";

    public static final String QUOTE_URL =
            "https://finnhub.io/api/v1/quote?symbol=";

    public static final String METRIC_URL =
            "https://finnhub.io/api/v1/stock/metric?symbol=";


    public static Stock getStockValue(String symbol) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        symbol = symbol.toUpperCase();
        // -----------------------------
        // COMPANY PROFILE
        // -----------------------------
        String profileString = IOUtils.toString(
                new URL(
                        PROFILE_URL
                                + symbol
                                + "&token="
                                + PUBLIC_KEY
                )
        );
        JsonNode profile = mapper.readTree(profileString);
        // -----------------------------
        // CURRENT STOCK QUOTE
        // -----------------------------
        String quoteString = IOUtils.toString(
                new URL(
                        QUOTE_URL
                                + symbol
                                + "&token="
                                + PUBLIC_KEY
                )
        );
        JsonNode quote = mapper.readTree(quoteString);
        // -----------------------------
        // BASIC FINANCIAL INFORMATION
        // -----------------------------
        String metricString = IOUtils.toString(
                new URL(
                        METRIC_URL
                                + symbol
                                + "&metric=all"
                                + "&token="
                                + PUBLIC_KEY
                )
        );
        JsonNode financialData = mapper.readTree(metricString);
        JsonNode metrics = financialData.get("metric");
        // -----------------------------
        // PARSE STOCK INFORMATION
        // -----------------------------
        String stockSymbol = profile.get("ticker").asString();
        String companyName = profile.get("name").asString();
        double currentPrice = quote.get("c").asDouble();

        // Finnhub returns market capitalization in millions
        double marketCapMillions =
                profile.get("marketCapitalization").asDouble();

        long marketCap =
                (long) (marketCapMillions * 1_000_000);

        // Finnhub average volume is represented in millions
        double volumeMillions =
                metrics.get("10DayAverageTradingVolume").asDouble();

        long volume =
                (long) (volumeMillions * 1_000_000);

        // Dividend yield is normally given as a percentage
        double dividendYieldPercent =
                metrics.get("dividendYieldIndicatedAnnual").asDouble();

        // Convert percentage to decimal representation
        double dividendYield =
                dividendYieldPercent / 100.0;


        // -----------------------------
        // CREATE STOCK OBJECT
        // -----------------------------
        Stock stock = new Stock();

        stock.setSymbol(stockSymbol);
        stock.setCompanyName(companyName);
        stock.setCurrentPrice(currentPrice);
        stock.setVolume(volume);
        stock.setMarketCap(marketCap);
        stock.setDividendYield(dividendYield);

        return stock;
    }
}
