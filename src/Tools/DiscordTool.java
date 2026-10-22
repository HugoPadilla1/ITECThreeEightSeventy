package Tools;

import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

public class DiscordTool {

    public static final String DISCORD_BOT_TOKEN =
            "a2b0bbe2505be50fe7e0a46941b8d717caaeb94f8aba9b97f453eff575256d4c";
    public static final String MY_DISCORD_CHANNEL = "1002873236037574728";
    public static final String DISCORD_API_URL = "https://discord.com/api/v10/channels/";

    public static void sendMessgeToChannel(String channelID, String msg) {
        Unirest.post(DISCORD_API_URL + channelID + "/messages")
                .header("Authorization", "Bot " + DISCORD_BOT_TOKEN)
                .contentType("application/json")
                .body("{\"content\":\" " + msg  + " \"}")
                .asEmpty();
        Unirest.shutDown();
    }

}
