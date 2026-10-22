import Tools.JokeUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DiscordJokeTest {

    @Test
    public void sendJokeToDiscord(){
        String joke = JokeUtils.getJoke();
        System.out.println(joke);
        Assertions.assertTrue(!joke.equals(""));

//        String escape = StringEscapeUtils.escapeJson(joke);
//        System.out.println(escape);
    }
}
