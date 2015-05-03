package huji.ac.il.stick_defence;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
    private ArrayList<Sprite> bows = new ArrayList<>();
    private Context context;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context) {
        this.context = context;
        Tower leftTower = new Tower(context, Tower.Player.LEFT);
        Tower rightTower = new Tower(context, Tower.Player.RIGHT);

        towers.add(leftTower);
        towers.add(rightTower);

        bows.add(new Bow(context, Bow.Player.LEFT, leftTower.getTowerHeight()));
        bows.add(new Bow(context, Bow.Player.RIGHT, rightTower.getTowerHeight()));

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
        for (Sprite bow : this.getBows()){
            bow.update(System.currentTimeMillis());
        }
    }

    public void touchDown(float x, float y){
//        ((Bow) bows.get(0)).rotateLeft();

    }

    public void touchMove(int move) {
        if(move==1){
            ((Bow) bows.get(0)).rotateRight();
        }
        if(move==2){
            ((Bow) bows.get(0)).rotateLeft();
        }

        if(move==3){
            ((Bow) bows.get(0)).unStretch();
        }
        if(move==4){
            ((Bow) bows.get(0)).stretch();
        }

    }

    public void touchUp(){
        ((Bow) bows.get(0)).release();
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

    public ArrayList<Sprite> getBows() { return this.bows; }
}
