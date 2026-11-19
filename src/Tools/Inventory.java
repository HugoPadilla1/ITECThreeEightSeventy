package Tools;

import java.util.ArrayList;

public class Inventory implements InventoryInterface {

    private ArrayList<Game> games;

    public Inventory() {
        games = new ArrayList<>();
    }

    @Override
    public void add(Game game) {
        games.add(game);
    }

    @Override
    public void remove(Game game) {
        games.remove(game);
    }

    @Override
    public Game findCheapestGame() {
        if (games.isEmpty()) {
            return null;
        }

        Game cheapest = games.get(0);

        for (Game game : games) {
            if (game.getPrice() < cheapest.getPrice()) {
                cheapest = game;
            }
        }

        return cheapest;
    }

    @Override
    public Game findMostExpensiveGame() {
        if (games.isEmpty()) {
            return null;
        }

        Game expensive = games.get(0);

        for (Game game : games) {
            if (game.getPrice() > expensive.getPrice()) {
                expensive = game;
            }
        }

        return expensive;
    }

    @Override
    public double getAveragePriceOfAllGames() {
        if (games.isEmpty()) {
            return 0.0;
        }

        double total = 0;

        for (Game game : games) {
            total += game.getPrice();
        }

        return total / games.size();
    }
}
