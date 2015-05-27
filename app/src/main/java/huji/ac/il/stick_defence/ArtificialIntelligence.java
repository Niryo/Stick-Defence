package huji.ac.il.stick_defence;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yahav on 09/05/15.
 */
public class ArtificialIntelligence {
    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD
    }
    //================================Easy======================================
    private static final float EASY_SECONDS_TO_SEND_SOLDIER = 4.0f;
    private static final float EASY_SECONDS_TO_SHOOT = 3.0f;
    private static final float EASY_SCREEN_PORTION_TO_AIM = 0.3f;

    //===============================Medium=====================================
    private static final float MEDUIM_SECONDS_TO_SEND_SOLDIER = 3.0f;
    private static final float MEDIUM_SECONDS_TO_SHOOT = 3.0f;
    private static final float MEDIUM_SCREEN_PORTION_TO_AIM = 0.2f;

    //================================Hard======================================
    private static final float HARD_SECONDS_TO_SEND_SOLDIER = 2.0f;
    private static final float HARD_SECONDS_TO_SHOOT = 3.0f;
    private static final float HARD_SCREEN_PORTION_TO_AIM = 0.1f;

    GameState gameState = GameState.getInstance();
    private Difficulty difficulty;

    private long  lastSoldierInMillisec;
    private long  lastShootInMillisec;
    private float secondsToSendSoldier;
    private float secondsToShoot;
    private int   pixelsShootRange;

    ArtificialIntelligence(Difficulty difficulty){
        this.difficulty = difficulty;
        int screenWidth = gameState.getContext().getResources().
                getDisplayMetrics().widthPixels;

        switch (difficulty){
            case EASY:
                secondsToSendSoldier = EASY_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = EASY_SECONDS_TO_SHOOT;
                pixelsShootRange =
                        (int) (screenWidth * EASY_SCREEN_PORTION_TO_AIM);
                break;
            case MEDIUM:
                secondsToSendSoldier = MEDUIM_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = MEDIUM_SECONDS_TO_SHOOT;
                pixelsShootRange =
                        (int) (screenWidth * MEDIUM_SCREEN_PORTION_TO_AIM);
                break;
            case HARD:
                secondsToSendSoldier = HARD_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = HARD_SECONDS_TO_SHOOT;
                pixelsShootRange =
                        (int) (screenWidth * HARD_SCREEN_PORTION_TO_AIM);
                break;
        }


    }

    public void sendSoldier(){
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastSoldierInMillisec)/1000 >= secondsToSendSoldier){
            gameState.addSoldier(Sprite.Player.RIGHT, System.currentTimeMillis());
            this.lastSoldierInMillisec = currentTime;
        }
    }

    public void shoot(){

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastShootInMillisec)/1000 >= secondsToShoot){
            this.lastShootInMillisec = currentTime;
            ArrayList<Soldier> soldiers = gameState.getSoldiers();
            Bow aiBow = gameState.getBows().get(1);
            for (Soldier soldier : soldiers){
                if (Sprite.Player.LEFT == soldier.getPlayer()){
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
