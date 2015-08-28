package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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

    //Soldiers prices
    private static final int ZOMBIE_SEND_PRICE = 50;
    private static final int SWORDMAN_SEND_PRICE = 5;
    private static final int BOMB_GRANDPA_SEND_PRICE = 5;
    private static final int BAZOOKA_SEND_PRICE = 10;
    private static final int TANK_SEND_PRICE = 100;

    //Progress buttons
    public static final int MILLISEC_TO_BASIC_SOLDIER = 1000;
    public static final int MILLISEC_TO_ZOMBIE = 2000;
    public static final int MILLISEC_TO_SWORDMAN = 3000;
    public static final int MILLISEC_TO_BOMB_GRANDPA = 1500;
    public static final int MILLISEC_TO_BAZOOKA = 4000;
    public static final int MILLISEC_TO_TANK = 5000;

    private static final int START_CREDITS = 200;

    private static int canvas_height;
    private static int canvas_width;
    private ArtificialIntelligence ai;
    private Sounds sounds= Sounds.getInstance();
    private ArrayList<Button> buttonsComponent;
    private ArrayList<Soldier> soldiers = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>(2);
    private ArrayList<Bow> bows = new ArrayList<>();
    private ArrayList<Arrow> arrows = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<DrawableObject> miscellaneous = new ArrayList<>();
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
    private PlayerStorage playerStorage, rightPlayerStorage;
    private Button sendBazookaSoldierButton;
    private Activity gameActivity;
    private Tower rightTower;
    private Tower leftTower;
    private boolean isInitialized=false;


    /**
     * Constructor. Adds 2 towers to the sprites list.
     *
     * @param context the context
     */
    private GameState(Context context, boolean isMultiplayer) {
        this.context = context;
        playerStorage = new PlayerStorage(context, 0);
        rightPlayerStorage = new PlayerStorage(context, 0);
        this.creditManager = new CreditManager(START_CREDITS);
        this.isMultiplayer = isMultiplayer;
 //       playerStorage = PlayerStorage.load(context);
    }

    public static GameState CreateGameState(Context context,
                                            boolean isMultiplayer) {
        if (null == gameState) {
            gameState = new GameState(context, isMultiplayer);
        }
        return gameState;
    }

    public static GameState getInstance() {
        return gameState;
    }

    public boolean isGameInProcces() {
        return this.playerStorage.isGameInProcess();
    }

    public void reset() {
        buttonsComponent = null;
        soldiers = new ArrayList<>();
        towers = new ArrayList<>();
        arrows = new ArrayList<>();
        bullets = new ArrayList<>();
        rightPlayerSoldiers = leftPlayerSoldiers = 0;
        leftProgressBar.setProgress(0);
        rightProgressBar.setProgress(0);
        leftPlayerWin = rightPlayerWin = false;

        leftTower = towerFactory(playerStorage, Sprite.Player.LEFT);
        rightTower = towerFactory(rightPlayerStorage, Sprite.Player.RIGHT);
        this.leftBow = new Bow(context, Sprite.Player.LEFT, leftTower);
        bows.set(0, leftBow);

        towers.add(leftTower);
        towers.add(rightTower);

        rightTowerLeftX = rightTower.getLeftX();
        leftTowerBeginX = leftTower.getRightX();
        rightTowerCentralX = rightTower.getCentralX();
        leftTowerCentralX = leftTower.getCentralX();

        this.rightBow = new Bow(context, Sprite.Player.RIGHT, rightTower);
        bows.set(1, rightBow);
    }

    public void init(int canvasWidth, int canvasHeight, Activity activity) {
        setCanvasDimentions(canvasWidth, canvasHeight);

        this.leftTower = towerFactory(playerStorage, Sprite.Player.LEFT);
        this.rightTower = towerFactory(rightPlayerStorage, Sprite.Player.RIGHT);
        this.gameActivity = activity;

        Log.w("yahav", "Adding" + leftTower.getName());
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

        if (!isMultiplayer){
            ai = new ArtificialIntelligence();
        }
        this.isInitialized = true;
    }

    private Tower towerFactory(PlayerStorage ps, Sprite.Player player){
        if (ps.isPurchased(PlayerStorage.PurchasesEnum.FORTIFIED_TOWER)){
            return new FortifiedTower(context, player);
        }
        if (ps.isPurchased(PlayerStorage.PurchasesEnum.STONE_TOWER)){
            return new StoneTower(context, player);
        }
        if (ps.isPurchased(PlayerStorage.PurchasesEnum.BIG_WOODEN_TOWER)){
            return new BigWoodenTower(context, player);
        }
        return new WoodenTower(context, player);
    }


    public void setSinglePlayer() {
        this.isMultiplayer = false;
    }

    public void sendFog(){
        client.reportFog();
    }
    public void addFog(){
        this.miscellaneous.add(new Fog(context));
    }
    public void finishGame() {
        playerStorage.setCredits(creditManager.getCredits());
    //    save();
        creditManager.setRunning(false);
    }

    public void save() {
        playerStorage.save();
    }

    public boolean isPurchased(PlayerStorage.PurchasesEnum iSoldier) {
        return this.playerStorage.isPurchased(iSoldier);
    }

    public void buyItem(PlayerStorage.PurchasesEnum iItem, int price) {
        this.playerStorage.buy(iItem);
        this.playerStorage.setCredits(this.playerStorage.getCredits() - price);
    }

    public void activateSendSoldierButton(Button button,
                                          PlayerStorage.PurchasesEnum soldierType) {
        if (playerStorage.isPurchased(soldierType)) {
            button.setVisibility(View.VISIBLE);
        }
    }

    public void initProgressBar(ProgressBar progressBar, Sprite.Player player) {

        if (Sprite.Player.LEFT == player) {
            progressBar.setMax((int) leftTower.getMaxHp());
            progressBar.setProgress((int) leftTower.getMaxHp());
            leftProgressBar = progressBar;
        } else {
            progressBar.setMax((int) rightTower.getMaxHp());
            progressBar.setProgress((int) rightTower.getMaxHp());
            rightProgressBar = progressBar;
        }
    }

    public void initCredits(TextView leftCreditsTv) {
        creditManager.initCreditTv(leftCreditsTv);
        creditManager.setRunning(true);
        creditManager.start();
    }

    public void addCredits(double creditsToAdd) {
        creditManager.addCredits(creditsToAdd);
    }

    public boolean decCredits(double creditsToDec){
        return creditManager.decCredits(creditsToDec, Sprite.Player.LEFT);
    }

    public int getCredits() {
        return creditManager.getCredits();
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
        for(DrawableObject drawableObject: this.miscellaneous){
            drawableObject.update(currentTimeMillis);
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
    public boolean addSoldier(Sprite.Player player, long timeStamp,
                           final Protocol.Action soldierType) {
        double delay = 0;
        if (player == Sprite.Player.LEFT) { // Us
            if (this.leftPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER) {
                return false;
            }
        } else {
            if (this.rightPlayerSoldiers >= MAX_SOLDIERS_PER_PLAYER) {
                return false;
            }
            if (isMultiplayer){
                long currentTime = getSyncTime();
                delay = currentTime - timeStamp;
                if (delay < 0) {
                    delay = 0;
                }
                delay = delay / 1000; //convert to seconds;

                Log.w("custom", "the delay is: " + delay);
            }



        }

        Soldier soldier;
        switch (soldierType) {
            case BASIC_SOLDIER:
                soldier = new BasicSoldier(context, player, delay);
                soldier.playSound();
                soldiers.add(soldier);
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    Log.w("yahav", "Basic soldier");
                    client.reportBasicSoldier();
                }
                break;
            case ZOMBIE:
                soldier =new Zombie(context, player, delay);

                if (player == Sprite.Player.RIGHT ||
                        creditManager.decCredits(ZOMBIE_SEND_PRICE, player)) {
                    soldiers.add(soldier);
                    soldier.playSound();
                } else {
                    return false;
                }
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    client.reportZombie();
                }
                break;
            case SWORDMAN:
                soldier =new Swordman(context, player, delay);

                if (player == Sprite.Player.RIGHT ||
                        creditManager.decCredits(SWORDMAN_SEND_PRICE, player)) {
                    soldiers.add(soldier);
                    soldier.playSound();
                } else {
                    return false;
                }
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    client.reportSwordman();
                }
                break;
            case BOMB_GRANDPA:
               soldier =new BombGrandpa(context, player, delay);

                if (player == Sprite.Player.RIGHT ||
                        creditManager.decCredits(BOMB_GRANDPA_SEND_PRICE, player)) {
                    soldiers.add(soldier);
                    soldier.playSound();
                } else {
                    return false;
                }
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    client.reportBombGrandpa();
                }
                break;
            case BAZOOKA_SOLDIER:
                soldier =new BazookaSoldier(context, player, delay);

                soldier.playSound();
                if (player == Sprite.Player.RIGHT ||
                        creditManager.decCredits(BAZOOKA_SEND_PRICE, player)) {
                    soldiers.add(soldier);
                    soldier.playSound();
                } else {
                    return false;
                }
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    client.reportBazookaSoldier();
                }
                break;
            case TANK:
                soldier =new Tank(context, player, delay);

                if (player == Sprite.Player.RIGHT ||
                        creditManager.decCredits(TANK_SEND_PRICE, player)) {
                    soldiers.add(soldier);
                    soldier.playSound();
                } else {
                    return false;
                }
                if (isMultiplayer && Sprite.Player.LEFT == player){
                    client.reportTank();
                }
                break;

            default:
                Log.e("yahav",
                        "Wrong soldier type " + soldierType.toString());
                break;
        }

        if (Sprite.Player.LEFT == player){
            this.leftPlayerSoldiers++;
        } else {
            this.rightPlayerSoldiers++;
        }

        return true;
    }
    public void newPartnerInfo(String rawInput){
        try {
            JSONObject info = new JSONObject(rawInput);
            String towerName= info.getString("tower");

            Tower.TowerTypes towerType =
                    Tower.TowerTypes.valueOf(towerName);
            switch (towerType){
                case BIG_WOODEN_TOWER:
                    rightPlayerStorage.buy(PlayerStorage.
                            PurchasesEnum.BIG_WOODEN_TOWER);
                    break;
                case STONE_TOWER:
                    rightPlayerStorage.buy(PlayerStorage.
                            PurchasesEnum.STONE_TOWER);
                    break;
                case FORTIFIED_TOWER:
                    rightPlayerStorage.buy(PlayerStorage.
                            PurchasesEnum.FORTIFIED_TOWER);
                    break;
                default:
                    rightPlayerStorage.buy(PlayerStorage.
                            PurchasesEnum.WOODEN_TOWER);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void sendInfoToPartner(Tower.TowerTypes type){
        JSONObject info = new JSONObject();
        try {
            info.put("tower", type.name());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String data= info.toString();
        client.send(Protocol.stringify(Protocol.Action.PARTNER_INFO, data));

    }

    public void removeSoldier(Soldier soldier, boolean shouldReport) {
        if (soldier.getPlayer() == Sprite.Player.LEFT) {
            this.leftPlayerSoldiers--;
        } else {
            this.rightPlayerSoldiers--;
            addCredits(10);
        }
        if (shouldReport && isMultiplayer){
            client.reportSoldierKill(soldier.getId(), soldier.getPlayer());
        }
        soldier.stopSound();
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

    public boolean isInitialized(){
        return this.isInitialized;
    }
    public void addArrow(Arrow arrow) {
        this.arrows.add(arrow);
        //todo:wait for server aproval
        if (isMultiplayer && arrow.getPlayer() == Sprite.Player.LEFT) {
            client.reportArrow(this.leftBow.getRelativeDistance());
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

        if (player == Sprite.Player.RIGHT) {
            if (!towers.get(0).reduceHP(hp)) {
                this.rightPlayerWin = true;
            }
        } else {
            addCredits(hp);
            if (!towers.get(1).reduceHP(hp)) {
                this.leftPlayerWin = true;
                addCredits(CREDITS_ON_WIN);
            }
        }
    }

    public boolean isRightPlayerWin() {
        return this.rightPlayerWin;
    }

    public boolean isLeftPlayerWin() {
        return this.leftPlayerWin;
    }

    public void startFastCreditMode(){
        creditManager.startFastCreditMode();
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

    public ArrayList<DrawableObject> getMiscellaneous(){
        return this.miscellaneous;
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

    public Tower.TowerTypes getLeftTowerType(){
        if (null == this.leftTower){
            return Tower.TowerTypes.WOODEN_TOWER;
        }
        return this.leftTower.getTowerType();
    }

    public ArtificialIntelligence getAi(){
        return ai;
    }

    public PlayerStorage getRightPlayerStorage(){
        return this.rightPlayerStorage;
    }
}
