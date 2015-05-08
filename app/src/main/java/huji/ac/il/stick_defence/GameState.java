package huji.ac.il.stick_defence;

import android.content.Context;
import android.gesture.Gesture;

import java.util.ArrayList;


/**
 * This class represents the game state of stick-defence game.
 * That is contains all sprites, handle them and manage
 * the interactions between them.
 */
public class GameState {
    private static GameState        gameState;

    private static int MAX_SOLDIERS_PER_PLAYER = 5;

    private ArrayList<Soldier>      soldiers = new ArrayList<>();
    private ArrayList<Tower>        towers = new ArrayList<>();
    private ArrayList<Bow>          bows = new ArrayList<>();
    private ArrayList<Arrow>        arrows= new ArrayList<>();
    private Context                 context;
    private int                     rightTowerLeftX;
    private int                     leftTowerBeginX;
    private int                     rightPlayerSoldiers = 0;
    private int                     leftPlayerSoldiers = 0;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context) {
        this.context = context;
    }
    private void init(){
        Tower leftTower = new Tower(context, Sprite.Player.LEFT);
        Tower rightTower = new Tower(context, Sprite.Player.RIGHT);

        towers.add(leftTower);
        towers.add(rightTower);

        bows.add(new Bow(context, Sprite.Player.LEFT, leftTower.getTowerHeight()));
//        bows.add(new Bow(context, Sprite.Player.RIGHT, rightTower.getTowerHeight()));

        rightTowerLeftX = rightTower.getLeftX();
        leftTowerBeginX = leftTower.getRightX();
    }

    public static GameState CreateGameState(Context context) {
        if (gameState == null) {
            gameState = new GameState(context);
            gameState.init();
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
        for (Soldier soldier : this.getSoldiers()) {
            soldier.update(System.currentTimeMillis());
        }
        for (Bow bow : this.getBows()){
            bow.update(System.currentTimeMillis());
        }
        for (Arrow arrow: this.getArrows()){
            arrow.update(System.currentTimeMillis());
        }
        for (Tower tower : this.getTowers()){
            tower.update(System.currentTimeMillis());
        }
        this.checkHits();

    }

    private void checkHits(){
        for(Arrow arrow: this.getArrows()){
            for(Soldier soldier: this.getSoldiers()){
                boolean hit = soldier.checkHit(arrow);
                if(hit){
          //          removeArrow(arrow);
                    removeSoldier(soldier);
                }
            }
        }
    }
    public void touch(SimpleGestureDetector.Gesture move, Sprite.Point point) {
        if(move == SimpleGestureDetector.Gesture.DOWN){
            ((Bow) bows.get(0)).rotateRight();
        }
        if(move == SimpleGestureDetector.Gesture.UP){
            ((Bow) bows.get(0)).rotateLeft();
        }

        if(move == SimpleGestureDetector.Gesture.RIGHT){
            ((Bow) bows.get(0)).unStretch();
        }
        if(move == SimpleGestureDetector.Gesture.LEFT){
            ((Bow) bows.get(0)).stretch();
        }
        if(move == SimpleGestureDetector.Gesture.TOUCH_UP){
            ((Bow) bows.get(0)).release();
        }
        if (move == SimpleGestureDetector.Gesture.TOUCH_DOWN){
            ((Bow) bows.get(0)).setBowDirection(point);
        }
    }



    /**
     * adds a soldier to the requested PLAYER
     *
     * @param player the requested PLAYER
     */
    public void addSoldier(Sprite.Player player) {
        if (player == Sprite.Player.LEFT){
            if (this.leftPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER){
                return;
            }
            this.leftPlayerSoldiers++;
        } else {
            if (this.rightPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER){
                return;
            }
            this.rightPlayerSoldiers++;
        }
        soldiers.add(new BasicSoldier(context, player));
    }

    public void removeSoldier(Soldier soldier) {
        if (soldier.getPlayer() == Sprite.Player.LEFT){
            this.leftPlayerSoldiers--;
        } else {
            this.rightPlayerSoldiers--;
        }
        soldiers.remove(soldier);

    }

    /**
     * Returns the sprite list
     *
     * @return the sprite list
     */
    public ArrayList<Soldier> getSoldiers() {
        return (ArrayList<Soldier>) this.soldiers.clone();
    }

    public ArrayList<Tower> getTowers() {
        return this.towers;
    }

    public ArrayList<Bow> getBows() { return this.bows; }

    public int getRightTowerLeftX(){
        return this.rightTowerLeftX;
    }

    public int getLeftTowerRightX(){
        return this.leftTowerBeginX;
    }

    public void addArrow(float x, float y, float[] tan){
        this.arrows.add(new Arrow(context, x,y,tan));
    }
    public void removeArrow(Arrow arrow){
        this.arrows.remove(arrow);
    }
    public ArrayList<Arrow> getArrows(){
        return (ArrayList<Arrow>) this.arrows.clone();
    }

    public void hitTower(Sprite.Player player, double hp){
        if (player == Sprite.Player.RIGHT){
            towers.get(0).reduceHP(hp);
        } else {
            towers.get(1).reduceHP(hp);
        }
    }
}
