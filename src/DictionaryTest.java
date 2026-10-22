import Tools.DictionaryTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DictionaryTest {

    @Test
    public void testWord() throws Exception{
        String definition = DictionaryTool.getWordDefinition("epitome");

        System.out.println(definition);

        Assertions.assertNotNull(definition);
        Assertions.assertFalse(definition.isEmpty());

    }
}
