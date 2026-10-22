package Tools;

import org.apache.commons.io.IOUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URL;

public class DictionaryTool {

    public static final String DICTIONARY_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public static String getWordDefinition(String word) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String str = IOUtils.toString(new URL(DICTIONARY_URL + word));
        JsonNode root = mapper.readTree(str);
        return root.get(0).get("meanings").get(0).get("definitions").get(0).get("definition").asString();
    }
}
