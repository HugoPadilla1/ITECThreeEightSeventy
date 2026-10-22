import Tools.Investment;
import Tools.Stock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class InvestmentTest {

    private static Investment loadedInvestment;
    private Investment investment;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void loadActualCsv() throws Exception {
        // Run from the project root, where investments.csv is located.
        String csvUrl = Path.of("investments.csv").toUri().toString();
        loadedInvestment = new Investment();
        loadedInvestment.loadPortfolioFromWeb(csvUrl);
    }

    @BeforeEach
    void setUp() {
        // All tests that use this object only read it; no shared state is changed.
        investment = loadedInvestment;
    }

    @Test
    void loadPortfolioFromWeb_loadsActualCsv() {
        // Arrange: @BeforeAll loaded the real CSV containing AAPL, MSFT, NVDA.

        // Act
        Stock cheapest = investment.findCheapestStock();
        Stock mostExpensive = investment.findMostExpensiveStock();
        double totalValue = investment.getTotalValueOfInvestment();

        // Assert
        assertAll(
                () -> assertNotNull(cheapest),
                () -> assertNotNull(mostExpensive),
                () -> assertTrue(cheapest.getCurrentPrice() > 0),
                () -> assertTrue(mostExpensive.getCurrentPrice() > 0),
                () -> assertTrue(totalValue > 0)
        );
    }

    @Test
    void findCheapestStock_returnsValidStockFromCsv() {
        // Arrange
        Stock cheapest = investment.findCheapestStock();
        Stock mostExpensive = investment.findMostExpensiveStock();

        // Act
        double cheapestPrice = cheapest.getCurrentPrice();

        // Assert
        assertTrue(cheapestPrice <= mostExpensive.getCurrentPrice());
        assertTrue(cheapest.getSymbol().equals("AAPL")
                || cheapest.getSymbol().equals("MSFT")
                || cheapest.getSymbol().equals("NVDA"));
    }

    @Test
    void findMostExpensiveStock_returnsValidStockFromCsv() {
        // Arrange
        Stock cheapest = investment.findCheapestStock();
        Stock mostExpensive = investment.findMostExpensiveStock();

        // Act
        double mostExpensivePrice = mostExpensive.getCurrentPrice();

        // Assert
        assertTrue(mostExpensivePrice >= cheapest.getCurrentPrice());
        assertTrue(mostExpensive.getSymbol().equals("AAPL")
                || mostExpensive.getSymbol().equals("MSFT")
                || mostExpensive.getSymbol().equals("NVDA"));
    }

    @Test
    void getTotalValueOfInvestment_isWithinBoundsForCsvShareQuantities() {
        // Arrange: The CSV contains 2, 3, and 4 shares (9 shares total).
        double minimumPrice = investment.findCheapestStock().getCurrentPrice();
        double maximumPrice = investment.findMostExpensiveStock().getCurrentPrice();

        // Act
        double total = investment.getTotalValueOfInvestment();

        System.out.println("Total Value of Investment: " + investment.getTotalValueOfInvestment());
//        System.out.println(investment.getTotalValueOfInvestment());

        assertTrue(total >= 9 * minimumPrice);
        assertTrue(total <= 9 * maximumPrice);
    }

    @Test
    void newInvestment_beforeLoadingCsv_hasNoInvestedShares() throws Exception {
        // Arrange
        Investment emptyInvestment = new Investment();

        // Act
        Stock cheapest = emptyInvestment.findCheapestStock();
        Stock mostExpensive = emptyInvestment.findMostExpensiveStock();
        double total = emptyInvestment.getTotalValueOfInvestment();

        // Assert
        assertNull(cheapest);
        assertNull(mostExpensive);
        assertEquals(0.0, total, 0.000001);
    }

    @Test
    void loadPortfolioFromWeb_rejectsInvalidShareQuantity() throws Exception {
        // Arrange
        Path invalidCsv = tempDir.resolve("invalid-shares.csv");
        Files.writeString(invalidCsv, "AAPL,notANumber\n");
        Investment invalidInvestment = new Investment();

        // Act and Assert
        assertThrows(NumberFormatException.class,
                () -> invalidInvestment.loadPortfolioFromWeb(invalidCsv.toUri().toString()));
    }

    @Test
    void loadPortfolioFromWeb_rejectsMissingCsvFile() throws Exception {
        // Arrange
        Path missingCsv = tempDir.resolve("does-not-exist.csv");
        Investment invalidInvestment = new Investment();

        // Act and Assert
        assertThrows(IOException.class,
                () -> invalidInvestment.loadPortfolioFromWeb(missingCsv.toUri().toString()));
    }

    @Test
    void loadPortfolioFromWeb_emptyCsv_keepsInvestmentEmpty() throws Exception {
        // Arrange
        Path emptyCsv = tempDir.resolve("empty.csv");
        Files.writeString(emptyCsv, "");
        Investment emptyInvestment = new Investment();

        // Act
        emptyInvestment.loadPortfolioFromWeb(emptyCsv.toUri().toString());

        // Assert
        assertNull(emptyInvestment.findCheapestStock());
        assertNull(emptyInvestment.findMostExpensiveStock());
        assertEquals(0.0, emptyInvestment.getTotalValueOfInvestment(), 0.000001);
    }


    @Test
    @SuppressWarnings("unchecked")
    void printPortfolioBreakdown_displaysSharesValuesAndAllocation() throws Exception {
        // Arrange: Use the exact shares and Finnhub prices loaded in @BeforeAll.
        // Investment has no public getters for its holdings, so the test reads
        // the existing private maps without changing production code.
        Field sharesField = Investment.class.getDeclaredField("shares");
        Field stocksField = Investment.class.getDeclaredField("stocks");
        sharesField.setAccessible(true);
        stocksField.setAccessible(true);

        Map<String, Integer> shares =
                (Map<String, Integer>) sharesField.get(investment);
        Map<String, Stock> stocks =
                (Map<String, Stock>) stocksField.get(investment);

        // Act
        double totalValue = investment.getTotalValueOfInvestment();
        int totalShares = 0;
        double calculatedTotal = 0.0;

        System.out.println("\n========== INVESTMENT PORTFOLIO ==========");
        System.out.printf("%-8s %-24s %7s %12s %14s %10s%n",
                "Symbol", "Company", "Shares", "Price", "Value", "Weight");
        System.out.println("-------------------------------------------------------------------------------");

        for (Map.Entry<String, Integer> entry : shares.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = stocks.get(symbol);
            double holdingValue = quantity * stock.getCurrentPrice();
            double allocation = totalValue == 0.0 ? 0.0 : 100.0 * holdingValue / totalValue;

            System.out.printf("%-8s %-24s %7d $%11.2f $%13.2f %9.2f%%%n",
                    symbol, stock.getCompanyName(), quantity,
                    stock.getCurrentPrice(), holdingValue, allocation);

            totalShares += quantity;
            calculatedTotal += holdingValue;
        }

        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf("Total shares: %d%n", totalShares);
        System.out.printf("Total portfolio value: $%,.2f%n", totalValue);
        System.out.printf("Portfolio composition: %d stocks (", shares.size());
        System.out.println(String.join(", ", shares.keySet()) + ")");
        System.out.println("Weights represent each holding's share of the total portfolio value.");

        // Assert: Validate the CSV positions and the displayed totals.
        final int actualShareCount = totalShares;
        final double actualCalculatedTotal = calculatedTotal;
        assertAll(
                () -> assertEquals(3, shares.size()),
                () -> assertEquals(2, shares.get("AAPL")),
                () -> assertEquals(3, shares.get("MSFT")),
                () -> assertEquals(4, shares.get("NVDA")),
                () -> assertEquals(9, actualShareCount),
                () -> assertEquals(actualCalculatedTotal, totalValue, 0.000001)
        );
    }

}
