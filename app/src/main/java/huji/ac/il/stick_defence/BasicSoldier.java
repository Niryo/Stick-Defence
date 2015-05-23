package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

/**
 * This class represents a basic soldier.
 */
public class BasicSoldier extends Soldier{

    //=======================BasicSoldier's abilities===========================
    private static final double TIME_TO_CROSS_SCREEN_IN_MILLI=10;
    private static final int    DAMAGE_PER_SEC = 1; // [Damage/Sec]
    //==========================================================================

    //==========================================================================
    //Soldier height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //soldier to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.166;
    //==========================================================================

    //============================Sprite constants==============================
    private static final int    NUMBER_OF_FRAMES = 9;
    private static final int    ANIMATION_SPEED = 4;
    //==========================================================================

    private static Bitmap         leftSoldierPic = null;
    private static Bitmap         rightSoldierPic = null;
    private static Bitmap         leftAttackSoldierPic = null;
    private static Bitmap         rightAttackSoldierPic = null;

    private Sprite.Player player;

    public BasicSoldier(Context context, Sprite.Player player,double delayInSec) {
        super(context, player, TIME_TO_CROSS_SCREEN_IN_MILLI, DAMAGE_PER_SEC, delayInSec);
        if  (null == leftSoldierPic) {
            leftSoldierPic = BitmapFactory.decodeResource(
                                                  context.getResources(),
                                                  R.drawable.basic_soldier_run);
        }

        if (null == rightSoldierPic){
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierPic);
        }

        if (null == leftAttackSoldierPic){
            leftAttackSoldierPic =
                    BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.basic_soldier_hit);
        }

        if(null == rightAttackSoldierPic){
            rightAttackSoldierPic = Sprite.mirrorBitmap(leftAttackSoldierPic);
        }

        if (Sprite.Player.LEFT == player){
            super.initSprite(context, leftSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, ANIMATION_SPEED);
        } else {
            super.initSprite(context, rightSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, ANIMATION_SPEED);
        }


        this.player = player;

    }

    public void update(long gameTime) {
//        if (!super.isAttack()){
//            if (player == Sprite.Player.LEFT){
//                if (getSoldierX() + getScaledFrameWidth() / 2 >=
//                        gameState.getRightTowerLeftX()){
//                    super.attack(leftAttackSoldierPic);
//                }
//            } else {
//                if (getSoldierX() + getScaledFrameWidth() / 2 <=
//                        gameState.getLeftTowerRightX()){
//                    super.attack(rightAttackSoldierPic);
//                }
//            }
//        }
        super.update(gameTime);
    }

    public void render(Canvas canvas) {
        super.render(canvas);
    }

    public boolean checkHit(Arrow arrow){
        return super.checkHit(arrow);
    }

}