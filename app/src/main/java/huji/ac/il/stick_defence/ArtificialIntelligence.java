package huji.ac.il.stick_defence;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yahav on 09/05/15.
 */
public class ArtificialIntelligence {

    private static final float START_SECONDS_TO_SEND_SOLDIER = 4.0f;
    private static final float START_SECONDS_TO_SHOOT = 3.0f;
    private static final float START_SCREEN_PORTION_TO_AIM = 0.3f;
    private static final float LEVEL_UP_FACTOR = 0.9f;

    GameState gameState;

    private long lastBasicSoldier, lastZombie, lastSwordman,
                 lastBombGrandpa, lastBazooka, lastTank;
    private long lastShootInMillisec;
    private float secondsToShoot;
    private int pixelsShootRange;
    private int level;
    private PlayerStorage aiStorage;

    ArtificialIntelligence() {
        gameState = GameState.getInstance();
        int screenWidth = gameState.getContext().getResources().
                getDisplayMetrics().widthPixels;

        secondsToShoot = START_SECONDS_TO_SHOOT;
        pixelsShootRange =
                (int) (screenWidth * START_SCREEN_PORTION_TO_AIM);
        lastBasicSoldier = lastZombie = lastSwordman = lastBazooka = lastTank =
                lastShootInMillisec = System.currentTimeMillis();
        aiStorage = gameState.getAiStorage();

        level = 0;
    }

    public void sendSoldier() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTank) >= GameState.MILLISEC_TO_TANK &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.TANK)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.TANK);
            lastTank = currentTime;
        } else if ((currentTime - lastBazooka) >=
                    GameState.MILLISEC_TO_BAZOOKA &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.BAZOOKA_SOLDIER);
            lastBazooka = currentTime;
        } else if ((currentTime - lastBombGrandpa) >=
                    GameState.MILLISEC_TO_BOMB_GRANDPA &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.BOMB_GRANDPA)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.BOMB_GRANDPA);
            lastBombGrandpa = currentTime;
        } else if ((currentTime - lastSwordman) >=
                GameState.MILLISEC_TO_SWORDMAN &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.SWORDMAN)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.SWORDMAN);
            lastSwordman = currentTime;
        } else if ((currentTime - lastZombie) >=
                GameState.MILLISEC_TO_ZOMBIE &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.ZOMBIE)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.ZOMBIE);
            lastZombie = currentTime;
        } else if ((currentTime - lastBasicSoldier) >=
                GameState.MILLISEC_TO_BASIC_SOLDIER &&
                aiStorage.isPurchased(PlayerStorage.PurchasesEnum.BASIC_SOLDIER)) {
            gameState.addSoldier(Sprite.Player.RIGHT, currentTime,
                    Protocol.Action.BASIC_SOLDIER);
            lastBasicSoldier = currentTime;
        }
    }


    public void shoot() {

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastShootInMillisec) / 1000 >= secondsToShoot) {
            this.lastShootInMillisec = currentTime;
            ArrayList<Soldier> soldiers = gameState.getSoldiers();
            Bow aiBow = gameState.getBows().get(1);
            for (Soldier soldier : soldiers) {
                if (Sprite.Player.LEFT == soldier.getPlayer()) {
                    int soldierX = soldier.getSoldierX();
                    int soldierY = soldier.getSoldierY();
                    int inaccuracy = randInt(-pixelsShootRange,
                            pixelsShootRange);
                    Sprite.Point point =
                            new Sprite.Point(soldierX + inaccuracy, soldierY);
                    aiBow.setBowDirection(point);

                    //Stretch until the bow is ready to shoot
                    aiBow.stretch();
                    aiBow.stretch();
                    aiBow.stretch();
                    aiBow.stretch();
                    aiBow.stretch();

                    //Compensation on bow angle inaccuracy
                    aiBow.rotateRight();
                    aiBow.rotateRight();
                    aiBow.rotateRight();

                    aiBow.release();

                    return;
                }
            }

        }

    }

    public void levelUp(){
        this.level++;

        switch (this.level){
            case 1:
                aiStorage.buy(PlayerStorage.PurchasesEnum.ZOMBIE);
                break;
            case 2:
                aiStorage.buy(PlayerStorage.PurchasesEnum.BIG_WOODEN_TOWER);
                break;
            case 3:
                aiStorage.buy(PlayerStorage.PurchasesEnum.SWORDMAN);
                break;
            case 4:
                aiStorage.buy(PlayerStorage.PurchasesEnum.STONE_TOWER);
                break;
            case 5:
                aiStorage.buy(PlayerStorage.PurchasesEnum.BOMB_GRANDPA);
                break;
            case 6:
                aiStorage.buy(PlayerStorage.PurchasesEnum.FORTIFIED_TOWER);
                break;
            case 7:
                aiStorage.buy(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER);
                break;
            case 8:
                aiStorage.buy(PlayerStorage.PurchasesEnum.TANK);
                break;
        }

        this.pixelsShootRange*=LEVEL_UP_FACTOR;
        this.secondsToShoot*=LEVEL_UP_FACTOR;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
