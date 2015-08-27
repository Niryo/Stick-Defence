package huji.ac.il.stick_defence;

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

    GameState gameState = GameState.getInstance();

    private long lastSoldierInMillisec;
    private long lastShootInMillisec;
    private float secondsToSendSoldier;
    private float secondsToShoot;
    private int pixelsShootRange;

    ArtificialIntelligence() {
        int screenWidth = gameState.getContext().getResources().
                getDisplayMetrics().widthPixels;

        secondsToSendSoldier = START_SECONDS_TO_SEND_SOLDIER;
        secondsToShoot = START_SECONDS_TO_SHOOT;
        pixelsShootRange =
                (int) (screenWidth * START_SCREEN_PORTION_TO_AIM);
        lastSoldierInMillisec = lastShootInMillisec = System.currentTimeMillis();
    }

    public void sendSoldier() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastSoldierInMillisec) / 1000 >= secondsToSendSoldier) {
            gameState.addSoldier(Sprite.Player.RIGHT, System.currentTimeMillis(),
                    Protocol.Action.BASIC_SOLDIER);
            //TODO - change to required soldier
            this.lastSoldierInMillisec = currentTime;
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
        this.pixelsShootRange*=LEVEL_UP_FACTOR;
        this.secondsToSendSoldier*=LEVEL_UP_FACTOR;
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
