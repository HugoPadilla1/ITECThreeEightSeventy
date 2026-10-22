//package Tests;
//
//import org.apache.commons.io.IOUtils;
//import tools.jackson.databind.JsonNode;
//import tools.jackson.databind.ObjectMapper;
//
//import java.net.URL;
//
//
//public class WeatherTest {
//
//    public static final String HOME_WEATHER_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/unctuous";
//
//    public static int getHomeWeather throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        String str = IOUtils.toString(new URL(HOME_WEATHER_URL));
//        JsonNode root = mapper.readTree(str);
//        int temp = root.get("properties").get("periods").get(0).get("temperature").asInt();
//        return temp;
//    }
//}
