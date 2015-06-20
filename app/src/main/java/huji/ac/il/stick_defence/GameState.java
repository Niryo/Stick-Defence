package huji.ac.il.stick_defence;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class represents the game state of stick-defence game.
 * That is contains all sprites, handle them and manage
 * the interactions between them.
 */
public class GameState implements Serializable{
    private static GameState gameState;
    public static final String FILE_NAME = "game_state.sav";

    private static int MAX_SOLDIERS_PER_PLAYER = 20;

    private ArrayList<Soldier> soldiers = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<Bow> bows = new ArrayList<>();
    private ArrayList<Arrow> arrows = new ArrayList<>();
    private ArrayList<BazookaBullet> bazookaBullets = new ArrayList<>();
    private Context context;
    private int rightTowerLeftX;
    private int leftTowerBeginX;
    private int rightPlayerSoldiers = 0;
    private int leftPlayerSoldiers = 0;
    private Bow leftBow;
    private Bow rightBow;
    private ProgressBar leftProgressBar;
    private ProgressBar rightProgressBar;
    private Client client = Client.getClientInstance();
    private long timeDifference;
    private boolean isMultiplayer = true;
    private boolean leftPlayerWin = false;
    private boolean rightPlayerWin = false;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context) {
        this.context = context;
    }

    public static GameState CreateGameState(Context context) {
        if (gameState == null) {
            gameState = load(context);
            if (null == gameState){
                gameState = new GameState(context);
                gameState.init();
            }

        }
        return gameState;
    }

    public static GameState getInstance() {
        return gameState;
    }

    public static void reset(){
        gameState = null;
    }

    private void init() {
        Tower leftTower = new Tower(context, Sprite.Player.LEFT);
        Tower rightTower = new Tower(context, Sprite.Player.RIGHT);

        towers.add(leftTower);
        towers.add(rightTower);

        this.leftBow = new Bow(context, Sprite.Player.LEFT, leftTower);
        this.rightBow = new Bow(context, Sprite.Player.RIGHT, rightTower);
        bows.add(this.leftBow);
        bows.add(this.rightBow);


        rightTowerLeftX = rightTower.getLeftX();
        leftTowerBeginX = leftTower.getRightX();
    }

    public void setSinglePlayer(){
        this.isMultiplayer = false;
    }

    public void initProgressBar(ProgressBar progressBar, Sprite.Player player) {
        progressBar.setMax((int) Tower.MAX_HP);
        progressBar.setProgress((int) Tower.MAX_HP);
        if (Sprite.Player.LEFT == player) {
            leftProgressBar = progressBar;
        } else {
            rightProgressBar = progressBar;
        }
    }

    public void setTowerProgressHP(double hp, Sprite.Player player) {
        if (Sprite.Player.LEFT == player) {
            leftProgressBar.setProgress((int) hp);
        } else {
            rightProgressBar.setProgress((int) hp);
        }
    }

    /**
     * Update the place and pictures of the sprites, but doesn't print them.
     */
    public void update() {
        long currentTimeMillis = System.currentTimeMillis();
        for (Soldier soldier : this.getSoldiers()) {
            soldier.update(currentTimeMillis);
        }
        for (BazookaBullet bullet : this.getBazookaBullets()){
            bullet.update(currentTimeMillis);
        }
        for (Bow bow : this.getBows()) {
            bow.update(currentTimeMillis);
        }
        for (Arrow arrow : this.getArrows()) {
            arrow.update(currentTimeMillis);
        }
        for (Tower tower : this.getTowers()) {
            tower.update(currentTimeMillis);
        }
        this.checkHits();
    }

    public void resetUpdateTimes(){
        for (Soldier soldier : this.getSoldiers()) {
            soldier.resetUpdateTime();
        }
        for (Arrow arrow : this.getArrows()) {
            arrow.resetUpdateTime();
        }
    }

    private void checkHits() {
        for (Arrow arrow : this.getArrows()) {
            boolean hit = false;
            for (Soldier soldier : this.getSoldiers()) {
                hit = soldier.checkHit(arrow);
                if (hit) {
                    removeArrow(arrow);
                    removeSoldier(soldier);
                    break;
                }
            }
            if (hit){
                continue;
            }
        }
    }

    public void touch(SimpleGestureDetector.Gesture move, Sprite.Point point) {
        if (move == SimpleGestureDetector.Gesture.DOWN) {
            this.leftBow.unStretch();
       //     this.leftBow.rotateRight();
        }
        if (move == SimpleGestureDetector.Gesture.UP) {
            this.leftBow.stretch();
       //     this.leftBow.rotateLeft();
        }

        if (move == SimpleGestureDetector.Gesture.RIGHT) {
            this.leftBow.unStretch();
       //     this.leftBow.rotateLeft();
            // this.rightBow.rotateLeft();

        }
        if (move == SimpleGestureDetector.Gesture.LEFT) {
            this.leftBow.stretch();
        //    this.leftBow.rotateRight();
            //this.rightBow.rotateRight();
        }
        if (move == SimpleGestureDetector.Gesture.TOUCH_UP) {
            this.leftBow.release();
        }
        if (move == SimpleGestureDetector.Gesture.TOUCH_DOWN) {
            this.leftBow.setBowDirection(point);
        }
    }


    /**
     * adds a soldier to the requested PLAYER
     *
     * @param player the requested PLAYER
     */
    public void addSoldier(Sprite.Player player, long timeStamp,
                           final Protocol.Action soldierType) {
        double delay;
        long currentTime = getSyncTime();
        if (player == Sprite.Player.LEFT) { // Us
            delay = 0;
            if (isMultiplayer){
                switch (soldierType){
                    case BASIC_SOLDIER:
                        client.reportBasicSoldier();
                        break;
                    case BAZOOKA_SOLDIER:
                        client.reportBazookaSoldier();
                        break;
                    default:
                        Log.e("yahav",
                              "Wrong soldier type " + soldierType.toString());
                        return;
                }

            }

            if (this.leftPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER) {
                return;
            }
            this.leftPlayerSoldiers++;
        } else { // Opponent
            delay = currentTime - timeStamp;
            if(delay<0){
                delay=0;
            }
            delay = delay / 1000; //convert to seconds;
            Log.w("custom", "the delay is: " + delay);
            if (this.rightPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER) {
                return;
            }
            this.rightPlayerSoldiers++;
        }
        switch (soldierType){
            case BASIC_SOLDIER:
                soldiers.add(new BasicSoldier(context, player, delay));
                break;
            case BAZOOKA_SOLDIER:
                soldiers.add(new BazookaSoldier(context, player, delay));
                break;
            default:
                Log.e("yahav",
                        "Wrong soldier type " + soldierType.toString());
                break;
        }

    }

    public void removeSoldier(Soldier soldier) {
        if (soldier.getPlayer() == Sprite.Player.LEFT) {
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

    public ArrayList<BazookaBullet> getBazookaBullets(){
        return (ArrayList<BazookaBullet>)this.bazookaBullets.clone();
    }

    public ArrayList<Tower> getTowers() {
        return this.towers;
    }

    public ArrayList<Bow> getBows() {
        return this.bows;
    }

    public int getRightTowerLeftX() {
        return this.rightTowerLeftX;
    }

    public int getLeftTowerRightX() {
        return this.leftTowerBeginX;
    }


    public void addArrow(Arrow arrow) {
        this.arrows.add(arrow);
        if (isMultiplayer && arrow.getPlayer() == Sprite.Player.LEFT) {
            client.reportArrow(this.leftBow.getDistance());
        }
    }

    public void removeArrow(Arrow arrow) {
        this.arrows.remove(arrow);
    }

    public void addBazookaBullet(BazookaBullet bullet) {
        this.bazookaBullets.add(bullet);
    }

    public void removeBazookaBullet(BazookaBullet bullet) {
        this.bazookaBullets.remove(bullet);
    }

    public ArrayList<Arrow> getArrows() {
        return (ArrayList<Arrow>) this.arrows.clone();
    }

    public void hitTower(Sprite.Player player, double hp) {
        if (player == Sprite.Player.RIGHT) {
            if (!towers.get(0).reduceHP(hp)){
                this.rightPlayerWin = true;
            }
        } else {
            if (!towers.get(1).reduceHP(hp)){
                this.leftPlayerWin = true;
            }
        }
    }

    public boolean isRightPlayerWin(){
        return this.rightPlayerWin;
    }

    public boolean isLeftPlayerWin(){
        return this.leftPlayerWin;
    }

    public void addEnemyShot(int dist,double timeStamp) {
        long currentTime = getSyncTime();
        double delay = currentTime - timeStamp;
        if(delay<0){
            delay=0;
        }
        Log.w("custom", "delay is: "+ delay);
        //delay = currentTime - timeStamp;
        delay = delay / 1000; //convert to seconds;
        this.rightBow.aimAndShoot(dist, delay);
    }

    public void addEnemyBazookaBullet(){
        int screenWidth = context.getResources().getDisplayMetrics()
                .widthPixels;
        BazookaBullet bullet = new BazookaBullet(getContext(), screenWidth / 2,
                                                 BazookaSoldier.getBazookaSoldierY(),
                                                 Sprite.Player.LEFT);
        addBazookaBullet(bullet);
    }

    public Context getContext() {
        return this.context;
    }

    public void setTime(long localTimeInMillisecond, long serverTimeInMillisecond) {
        this.timeDifference = serverTimeInMillisecond - localTimeInMillisecond;
        Log.w("custom", "time deference is: "+ timeDifference);
    }

    private long getSyncTime() {
        return System.currentTimeMillis() + this.timeDifference;
    }

    public boolean isMultiplayer(){
        return this.isMultiplayer;
    }

    private boolean isGameOver(){
        return isRightPlayerWin() || isLeftPlayerWin();
    }

    public void save(File file){
        if (isGameOver()){
            Log.w("yahav", "Game over, don't save");
            return;
        }
        try{
            Log.w("yahav", "Saving to" + context.getFilesDir());
            ObjectOutputStream oos =
                    new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static GameState load(Context context){
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (file.exists()){
            Log.w("yahav", "Loading from" + context.getFilesDir());
            try{
                ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(
                                new File(context.getFilesDir(),
                                        FILE_NAME)));
                GameState gameState = (GameState) ois.readObject();
                ois.close();
                return gameState;
            } catch (IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e){
                e.printStackTrace(); //May be deleted
            }
        } else {
            Log.w("yahav", "Tried to load a nonexistent file");
        }

        return null;
    }

    public boolean isGameInProcces(){
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.exists();
    }
}
