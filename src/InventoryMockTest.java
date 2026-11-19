import Tools.Game;
import Tools.Inventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryMockTest {

    @Test
    public void testFindCheapestGame() {
        // Arrange
        Inventory inventory = mock(Inventory.class);
        Game cheapest = new Game("Madden NFL", 30.0);

        when(inventory.findCheapestGame()).thenReturn(cheapest);

        // Act
        Game result = inventory.findCheapestGame();

        // Assert
        assertEquals("Madden NFL", result.getName());
        verify(inventory).findCheapestGame();
    }

    @Test
    public void testFindMostExpensiveGame() {
        // Arrange
        Inventory inventory = mock(Inventory.class);
        Game expensive = new Game("Super Mario Bros", 90.0);

        when(inventory.findMostExpensiveGame()).thenReturn(expensive);

        // Act
        Game result = inventory.findMostExpensiveGame();

        // Assert
        assertEquals("Super Mario Bros", result.getName());
        verify(inventory).findMostExpensiveGame();
    }

    @Test
    public void testGetAveragePriceOfAllGames() {
        // Arrange
        Inventory inventory = mock(Inventory.class);

        when(inventory.getAveragePriceOfAllGames())
                .thenReturn(60.0);

        // Act
        double result = inventory.getAveragePriceOfAllGames();

        // Assert
        assertEquals(60.0, result, 0.001);
        verify(inventory).getAveragePriceOfAllGames();
    }
}
