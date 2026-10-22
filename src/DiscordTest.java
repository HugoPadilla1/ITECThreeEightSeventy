    import Tools.DiscordTool;
    import Tools.StockMarketTool;
    import kong.unirest.core.Unirest;
    import org.junit.jupiter.api.Test;

    public class DiscordTest {

        @Test
        public void testSendMessage() {
            Unirest.post("https://discord.com/api/v10/channels/715078212333600810/774558828015386644")
                    .header("Authorization", "Bot " + "a2b0bbe2505be50fe7e0a46941b8d717caaeb94f8aba9b97f453eff575256d4c--AI")
                    .contentType("application/json")
                    .body("{\"content\": \"testing is fun!\"}")
                    .asEmpty();
            Unirest.shutDown();
        }

    //    @Test
    //    public void testStockMsg() throws Exception {
    //        double costco = StockMarketTool.getStockValue("COST");
    //        DiscordTool.sendMessgeToChannel(DiscordTool.MY_DISCORD_CHANNEL,
    //                "COST: " + costco);
    //    }
    }
