package huji.ac.il.stick_defence;

import android.content.Context;

import java.util.ArrayList;


/**
 * This class represents the game state of stick-defence game.
 * That is contains all sprites, handle them and manage
 * the interactions between them.
 */
public class GameState {
    private static GameState gameState;
    private ArrayList<Sprite> soldiers = new ArrayList<>();
    private ArrayList<Sprite> towers = new ArrayList<>();
    private Context context;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context) {
        this.context = context;
        towers.add(new Tower(context, Tower.Player.RIGHT));
        towers.add(new Tower(context, Tower.Player.LEFT));

        //     soldiers.add(new BasicSoldier(context, BasicSoldier.Player.LEFT));

    }

    public static GameState CreateGameState(Context context) {
        if (gameState == null) {
            gameState = new GameState(context);
        }
        return gameState;
    }

    public static GameState getInstance() {
        return gameState;
    }

    /**
     * Update the place and pictures of the sprites, but doesn't print them.
     */
    public void update() {
        for (Sprite sprite : this.getSoldiers()) {
            sprite.update(System.currentTimeMillis());
        }

    }

    /**
     * adds a soldier to the requested player
     *
     * @param player the requested player
     */
    public void addSoldier(BasicSoldier.Player player) {
        soldiers.add(new BasicSoldier(context, player));
    }

    public void removeSoldier(BasicSoldier soldier) {
        soldiers.remove(soldier);

    }

    /**
     * Returns the sprite list
     *
     * @return the sprite list
     */
    public ArrayList<Sprite> getSoldiers() {
        return (ArrayList<Sprite>) this.soldiers.clone();
    }

    public ArrayList<Sprite> getTowers() {
        return this.towers;
    }
}
