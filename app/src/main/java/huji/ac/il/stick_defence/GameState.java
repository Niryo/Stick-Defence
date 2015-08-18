package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * This class represents the game state of stick-defence game.
 * That is contains all sprites, handle them and manage
 * the interactions between them.
 */
public class GameState {


    private static GameState gameState;

    private static final int MAX_SOLDIERS_PER_PLAYER = 20;
    private static final int CREDITS_ON_WIN = 100;
    private static final int SWORDMAN_SEND_PRICE = 5;
    private static final int BAZOOKA_SEND_PRICE = 10;
    private static final int TANK_SEND_PRICE = 100;
    private static int canvas_height;
    private static int canvas_width;

    private ArrayList<Button> buttonsComponent;
    private ArrayList<Soldier> soldiers = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<Bow> bows = new ArrayList<>();
    private ArrayList<Arrow> arrows = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private Context context;
    private int rightTowerLeftX, leftTowerBeginX;
    private int rightTowerCentralX, leftTowerCentralX;
    private int rightPlayerSoldiers = 0;
    private int leftPlayerSoldiers = 0;
    private Bow leftBow, rightBow;
    private ProgressBar leftProgressBar, rightProgressBar;
    private CreditManager creditManager;
    private Client client = Client.getClientInstance();
    private long timeDifference;
    private boolean isMultiplayer = true;
    private boolean leftPlayerWin = false;
    private boolean rightPlayerWin = false;
    private PlayerStorage playerStorage;
    private Button sendBazookaSoldierButton;
    private Activity gameActivity;

    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context,Activity activity) {
        this.context = context;
        this.gameActivity=activity;
        playerStorage = PlayerStorage.load(context);
    }

    public static GameState CreateGameState(Context context,Activity activity, int canvasWidth, int canvasHeight) {
        if (null == gameState) {
            gameState = new GameState(context,activity);
            gameState.init(canvasWidth, canvasHeight);
        }

        return gameState;
    }

    public static GameState getInstance() {
        return gameState;
    }

    public boolean isGameInProcces() {
        return this.playerStorage.isGameInProcess();
    }

    public static void reset() {
        gameState = null;
    }

    private void init(int canvasWidth, int canvasHeight) {
        setCanvasDimentions(canvasWidth, canvasHeight);
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
        rightTowerCentralX = rightTower.getCentralX();
        leftTowerCentralX = leftTower.getCentralX();

    }

    public void setSinglePlayer() {
        this.isMultiplayer = false;
    }

    public void saveAndFinish() {
        playerStorage.setCredits(creditManager.getCredits(Sprite.Player.LEFT));
        save();
        creditManager.setRunning(false);
    }

    public void save() {
        playerStorage.save();
    }

    public boolean isPurchased(PlayerStorage.PurchasesEnum iSoldier) {
        return this.playerStorage.isPurchased(iSoldier);
    }

    public void buySoldier(PlayerStorage.PurchasesEnum iSoldier, int price) {
        this.playerStorage.buySoldier(iSoldier);
        this.playerStorage.setCredits(this.playerStorage.getCredits() - price);
    }

    public void activateSendSoldierButton(Button button,
                                          PlayerStorage.PurchasesEnum soldierType) {
        if (playerStorage.isPurchased(soldierType)) {
            button.setVisibility(View.VISIBLE);
        }
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

    public void initCredits(TextView leftCreditsTv) {
        this.creditManager =
                new CreditManager(leftCreditsTv,
                        this.playerStorage.getCredits(), 0); //TODO - support right player

        creditManager.setRunning(true);
        creditManager.start();
    }

    public void addCredits(double creditsToAdd, Sprite.Player player) {
        creditManager.addCredits(creditsToAdd, player);
    }

    public int getCredits(Sprite.Player player) {
        return creditManager.getCredits(player);
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
        for (Bullet bullet : this.getBullets()) {
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


    private void checkHits() {
        for (Arrow arrow : this.getArrows()) {
            boolean hit = false;
            for (Soldier soldier : this.getSoldiers()) {
                hit = soldier.isHitByArrow(arrow);
                if (hit) {
                    removeArrow(arrow);
                    removeSoldier(soldier, isMultiplayer);
                    break;
                }
            }
            if (hit) {
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
            if (isMultiplayer) {
                switch (soldierType) {
                    case BASIC_SOLDIER:
                        client.reportBasicSoldier();
                        break;
                    case SWORDMAN:
                        client.reportSwordman();
                        break;
                    case BAZOOKA_SOLDIER:
                        client.reportBazookaSoldier();
                        break;
                    case TANK:
                        client.reportTank();
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
            if (delay < 0) {
                delay = 0;
            }
            delay = delay / 1000; //convert to seconds;
            Log.w("custom", "the delay is: " + delay);
            if (this.rightPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER) {
                return;
            }
            this.rightPlayerSoldiers++;
        }
        switch (soldierType) {
            case BASIC_SOLDIER:
                soldiers.add(new BasicSoldier(context, player, delay));
                break;
            case SWORDMAN:
                if (creditManager.decCredits(SWORDMAN_SEND_PRICE, player) ||
                        player== Sprite.Player.RIGHT) {
                    soldiers.add(new Swordman(context, player, delay));
                }
                break;
            case BAZOOKA_SOLDIER:
                if (creditManager.decCredits(TANK_SEND_PRICE, player) || player== Sprite.Player.RIGHT) {
                    soldiers.add(new Tank(context, player, delay));
                }
                break;
            case TANK:
                if (creditManager.decCredits(BAZOOKA_SEND_PRICE, player) || player== Sprite.Player.RIGHT) {
                    soldiers.add(new BazookaSoldier(context, player, delay));
                }
                break;
            default:
                Log.e("yahav",
                        "Wrong soldier type " + soldierType.toString());
                break;
        }

    }

    public void removeSoldier(Soldier soldier, boolean shouldReport) {
        if (soldier.getPlayer() == Sprite.Player.LEFT) {
            this.leftPlayerSoldiers--;
            if (!isMultiplayer){
                addCredits(10, Sprite.Player.RIGHT);
            }
        } else {
            this.rightPlayerSoldiers--;
            addCredits(10, Sprite.Player.LEFT);
        }
        if (shouldReport){
            client.reportSoldierKill(soldier.getId(), soldier.getPlayer());
        }
        soldiers.remove(soldier);
    }

    public void removeSoldier(int soldierId,
                              Sprite.Player player) {
        for (Soldier soldier : soldiers){
            if (soldier.getId() == soldierId && soldier.getPlayer() == player){
                removeSoldier(soldier, false);
                Log.w("yahav", "Soldier" + soldier.getId() +
                        soldier.getPlayer().toString() +
                        " removed by requests from other peer");
                return;
            }
        }
    }

    /**
     * Returns the sprite list
     *
     * @return the sprite list
     */
    public ArrayList<Soldier> getSoldiers() {
        return (ArrayList<Soldier>) this.soldiers.clone();
    }

    public ArrayList<Bullet> getBullets() {
        return (ArrayList<Bullet>) this.bullets.clone();
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

    public int getRightTowerCentralX() {
        return this.rightTowerCentralX;
    }

    public int getLeftTowerCentralX() {
        return this.leftTowerCentralX;
    }

    public void sendMathBomb(){
        client.reportMathBomb();
    }

    public void addArrow(Arrow arrow) {
        this.arrows.add(arrow);
        //todo:wait for server aproval
        if (isMultiplayer && arrow.getPlayer() == Sprite.Player.LEFT) {
            client.reportArrow(this.leftBow.getRelativeDistance());
        }
    }

    /**
     * Kill a soldier received from server (if exists)
     * @param id The soldier id
     * @param player The player the soldier belong to
     */
    public void killSoldier(int id, Sprite.Player player, boolean shouldReport){
        for (int iSoldier = 0 ; iSoldier < soldiers.size() ; iSoldier ++){
            Soldier soldier = soldiers.get(iSoldier);
            if (soldier.getId() == id && soldier.getPlayer() == player){
                if (Sprite.Player.LEFT == soldier.getPlayer()){
                    this.leftPlayerSoldiers--;
                } else {
                    this.rightPlayerSoldiers--;
                    addCredits(10, Sprite.Player.RIGHT);
                }
                soldiers.remove(soldier);
            }
        }
    }

    public void removeArrow(Arrow arrow) {
        this.arrows.remove(arrow);
    }

    public void addBullet(Bullet bullet) {
        this.bullets.add(bullet);
    }

    public void removeBullet(Bullet bullet) {
        this.bullets.remove(bullet);
    }

    public ArrayList<Arrow> getArrows() {
        return (ArrayList<Arrow>) this.arrows.clone();
    }

    public void hitTower(Sprite.Player player, double hp) {
        if (this.rightPlayerWin || this.leftPlayerWin) {
            return;
        }
        addCredits(hp, player);
        if (player == Sprite.Player.RIGHT) {
            if (!towers.get(0).reduceHP(hp)) {
                this.rightPlayerWin = true;
                addCredits(CREDITS_ON_WIN, player);
            }
        } else {
            if (!towers.get(1).reduceHP(hp)) {
                this.leftPlayerWin = true;
                addCredits(CREDITS_ON_WIN, player);
            }
        }
    }

    public boolean isRightPlayerWin() {
        return this.rightPlayerWin;
    }

    public boolean isLeftPlayerWin() {
        return this.leftPlayerWin;
    }

    public void addEnemyShot(double dist, double timeStamp) {
        long currentTime = getSyncTime();
        double delay = currentTime - timeStamp;
        if (delay < 0) {
            delay = 0;
        }
        Log.w("custom", "delay is: " + delay);
        //delay = currentTime - timeStamp;
        delay = delay / 1000; //convert to seconds;
        this.rightBow.aimAndShoot(dist, delay);
    }

    public Context getContext() {
        return this.context;
    }

    public void setTime(long localTimeInMillisecond, long serverTimeInMillisecond) {
        this.timeDifference = serverTimeInMillisecond - localTimeInMillisecond;
        Log.w("custom", "time deference is: " + timeDifference);
    }

    private long getSyncTime() {
        return System.currentTimeMillis() + this.timeDifference;
    }

    public boolean isMultiplayer() {
        return this.isMultiplayer;
    }

    private boolean isGameOver() {
        return isRightPlayerWin() || isLeftPlayerWin();
    }

    public static void setCanvasDimentions(int width, int height) {
        canvas_height = height;
        canvas_width = width;
    }

    public static int getCanvasHeight() {
        return canvas_height;
    }

    public static int getCanvasWidth() {
        return canvas_width;
    }
    public void setButtonsComponent(ArrayList<Button> buttons){
        this.buttonsComponent= buttons;
    }

    public void disableButtons(){
        this.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for( Button btn : buttonsComponent){
                    btn.setEnabled(false);
                }
            }
        });



    }
    public void enableButtons(){
        this.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for( Button btn : buttonsComponent){
                    btn.setEnabled(true);
                }
            }
        });
    }
}
