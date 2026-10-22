import Tools.Portfolio;
import Tools.Stock;
import Tools.StockMarketTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PortfolioTest {


    @Test
    public void testPortfolioConstructor() throws Exception {

        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        Stock cheapest =
                portfolio.findCheapestStock();
        Stock mostExpensive =
                portfolio.findMostExpensiveStock();

        // Assert
        Assertions.assertNotNull(portfolio);
        Assertions.assertNotNull(cheapest);
        Assertions.assertNotNull(mostExpensive);
        Assertions.assertTrue(
                cheapest.getCurrentPrice() > 0
        );
        Assertions.assertTrue(
                mostExpensive.getCurrentPrice() > 0
        );
    }


    @Test
    public void testFindCheapestStock() throws Exception {

        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        Stock cheapest =
                portfolio.findCheapestStock();

        // Assert
        Assertions.assertNotNull(cheapest);
        Assertions.assertNotNull(
                cheapest.getSymbol()
        );
        Assertions.assertTrue(
                cheapest.getCurrentPrice() > 0
        );
        System.out.println(
                "Cheapest Stock:"
        );
        cheapest.printStock();
    }


    @Test
    public void testFindMostExpensiveStock()
            throws Exception {

        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        Stock mostExpensive =
                portfolio.findMostExpensiveStock();

        // Assert
        Assertions.assertNotNull(
                mostExpensive
        );
        Assertions.assertNotNull(
                mostExpensive.getSymbol()
        );
        Assertions.assertTrue(
                mostExpensive.getCurrentPrice() > 0
        );
        System.out.println(
                "Most Expensive Stock:"
        );
        mostExpensive.printStock();
    }


    @Test
    public void testCheapestIsLessThanMostExpensive()
            throws Exception {

        // Arrange
        Portfolio portfolio = new Portfolio();

        // Act
        Stock cheapest =
                portfolio.findCheapestStock();
        Stock mostExpensive =
                portfolio.findMostExpensiveStock();

        // Assert
        Assertions.assertTrue(
                cheapest.getCurrentPrice()
                        <=
                        mostExpensive.getCurrentPrice()
        );
    }


    @Test
    public void testAddStock() throws Exception {

        // Arrange
        Portfolio portfolio = new Portfolio();
        Stock amazon =
                StockMarketTool.getStockValue(
                        "AMZN"
                );

        // Act
        portfolio.add(amazon);

        // Assert
        Assertions.assertNotNull(amazon);
        Assertions.assertEquals(
                "AMZN",
                amazon.getSymbol()
        );
        Assertions.assertTrue(
                amazon.getCurrentPrice() > 0
        );
    }


    @Test
    public void testRemoveStock()
            throws Exception {

        // Arrange
        Portfolio portfolio =
                new Portfolio();
        Stock cheapest =
                portfolio.findCheapestStock();

        // Act
        portfolio.remove(cheapest);
        Stock newCheapest =
                portfolio.findCheapestStock();


        // Assert
        Assertions.assertNotNull(
                newCheapest
        );
        Assertions.assertNotEquals(
                cheapest,
                newCheapest
        );
    }


    @Test
    public void testRemoveAllStocks()
            throws Exception {

        // Arrange
        Portfolio portfolio =
                new Portfolio();
        Stock stock1 =
                portfolio.findCheapestStock();
        portfolio.remove(stock1);
        Stock stock2 =
                portfolio.findCheapestStock();
        portfolio.remove(stock2);
        Stock stock3 =
                portfolio.findCheapestStock();

        // Act
        portfolio.remove(stock3);

        // Assert
        Assertions.assertNull(
                portfolio.findCheapestStock()
        );
        Assertions.assertNull(
                portfolio.findMostExpensiveStock()
        );
    }


    @Test
    public void testEmptyPortfolioAverage()
            throws Exception {

        // Arrange
        Portfolio portfolio =
                new Portfolio();
        portfolio.remove(
                portfolio.findCheapestStock()
        );
        portfolio.remove(
                portfolio.findCheapestStock()
        );
        portfolio.remove(
                portfolio.findCheapestStock()
        );
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();
        PrintStream originalOutput =
                System.out;
        System.setOut(
                new PrintStream(output)
        );

        // Act
        portfolio.printAveragePriceOfAllStocks();
        System.setOut(originalOutput);

        // Assert
        Assertions.assertEquals(
                "0.00",
                output.toString().trim()
        );
    }


    @Test
    public void testPrintAveragePrice()
            throws Exception {

        // Arrange
        Portfolio portfolio =
                new Portfolio();
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();
        PrintStream originalOutput =
                System.out;
        System.setOut(
                new PrintStream(output)
        );

        // Act
        portfolio.printAveragePriceOfAllStocks();
        System.setOut(originalOutput);

        // Assert
        Assertions.assertFalse(
                output.toString()
                        .trim()
                        .isEmpty()
        );
        double average =
                Double.parseDouble(
                        output.toString().trim()
                );
        Assertions.assertTrue(
                average > 0
        );
    }
}