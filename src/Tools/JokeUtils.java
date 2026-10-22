package Tools;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URL;

public class JokeUtils {
    public static String getJoke(){

        ObjectMapper mapper = new ObjectMapper();
        try {
            URL url = new URL("https://v2.jokeapi.dev/joke/Any");
            JsonNode root = mapper.readTree(url.openStream());
            return root.get("joke").asString();
        } catch (Exception e){
            throw new RuntimeException("Joke Retrieval Error");
        }
    }
}
