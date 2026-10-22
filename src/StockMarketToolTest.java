import Tools.Stock;
import Tools.StockMarketTool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StockMarketToolTest {
    @Test
    public void testStocks() throws Exception {

        Stock[] stocks = new Stock[3];

        stocks[0] =
                StockMarketTool.getStockValue("AAPL");

        stocks[1] =
                StockMarketTool.getStockValue("MSFT");

        stocks[2] =
                StockMarketTool.getStockValue("NVDA");

        for (Stock stock : stocks) {
            stock.printStock();
            // Basic JUnit tests
            Assertions.assertNotNull(stock);
            Assertions.assertNotNull(stock.getSymbol());
            Assertions.assertNotNull(stock.getCompanyName());
            Assertions.assertTrue(
                    stock.getCurrentPrice() > 0
            );
        }
    }
}
