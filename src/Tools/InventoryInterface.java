package Tools;


public interface InventoryInterface {

    void add(Game game);

    void remove(Game game);

    Game findCheapestGame();

    Game findMostExpensiveGame();

    double getAveragePriceOfAllGames();
}
