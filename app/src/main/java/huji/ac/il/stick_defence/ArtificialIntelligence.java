package huji.ac.il.stick_defence;

import android.util.Log;

import java.util.ArrayList;

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
    private static final float EASY_SECONDS_TO_SEND_SOLDIER = 3.0f;
    private static final float EASY_SECONDS_TO_SHOOT = 3.0f;

    //===============================Medium=====================================
    private static final float MEDUIM_SECONDS_TO_SEND_SOLDIER = 2.5f;
    private static final float MEDIUM_SECONDS_TO_SHOOT = 3.0f;

    //================================Hard======================================
    private static final float HARD_SECONDS_TO_SEND_SOLDIER = 2.0f;
    private static final float HARD_SECONDS_TO_SHOOT = 3.0f;

    GameState gameState = GameState.getInstance();
    private Difficulty difficulty;

    private long lastSoldierInMillisec;
    private long lastShootInMillisec;
    private float secondsToSendSoldier;
    private float secondsToShoot;

    ArtificialIntelligence(Difficulty difficulty){
        this.difficulty = difficulty;

        switch (difficulty){
            case EASY:
                secondsToSendSoldier = EASY_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = EASY_SECONDS_TO_SHOOT;
                break;
            case MEDIUM:
                secondsToSendSoldier = MEDUIM_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = MEDIUM_SECONDS_TO_SHOOT;
                break;
            case HARD:
                secondsToSendSoldier = HARD_SECONDS_TO_SEND_SOLDIER;
                secondsToShoot = HARD_SECONDS_TO_SHOOT;
                break;
        }
    }

    public void sendSoldier(){
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastSoldierInMillisec)/1000 >= secondsToSendSoldier){
            gameState.addSoldier(Sprite.Player.RIGHT);
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
                    Sprite.Point point = new Sprite.Point(soldierX, soldierY);
                    aiBow.setBowDirection(point);
                    aiBow.stretch();
                    aiBow.release();
                    aiBow.unStretch();
                    return;
                }
            }

        }






    }
}
