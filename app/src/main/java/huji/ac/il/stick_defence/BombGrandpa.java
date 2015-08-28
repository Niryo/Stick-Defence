package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * This class represents a bomb grandpa.
 */
public class BombGrandpa extends Soldier {

    //=======================BombGrandpa's abilities===========================
    private static final double SEC_TO_CROSS_SCREEN = 30;
    private static final int DAMAGE_PER_SEC = 0; // [Damage/Sec]
    private static final int BOMB_DAMAGE = 100;
    //==========================================================================

    //==========================================================================
    //Soldier height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //soldier to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.150;
    //==========================================================================

    //============================Sprite constants==============================
    private static final int NUMBER_OF_FRAMES = 9;
    private static final int FIRE_FRAMES = 4;
    private static final int MOVE_FPS = 40;
    private static final int FIRE_FPS = 10;
    //==========================================================================

    private static Bitmap leftSoldierPic = null;
    private static Bitmap rightSoldierPic = null;
    private static Bitmap explosionPic = null;


    private Sprite.Player player;

    public BombGrandpa(Context context, Sprite.Player player, double delayInSec) {
        super(context, player, SEC_TO_CROSS_SCREEN, DAMAGE_PER_SEC,
                Sounds.WALKING_SOUND, delayInSec);
        if (null == leftSoldierPic) {
            leftSoldierPic = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.drawable.bomb_grandpa);
        }

        if (null == rightSoldierPic) {
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierPic);
        }

        if (null == explosionPic) {
            explosionPic = Tower.firePic;
        }
        
        if (Sprite.Player.LEFT == player) {
            super.initSprite(context, leftSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        } else {
            super.initSprite(context, rightSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        }


        this.player = player;


    }

    public void update(long gameTime) {
        if (!super.isAttack()) {
            if (player == Sprite.Player.LEFT) {
                if (getSoldierX() + getScaledFrameWidth() / 2 >=
                        gameState.getRightTowerLeftX()) {
                    super.attack(explosionPic, FIRE_FRAMES, FIRE_FPS);
                    super.setSoldierX(getSoldierX() +
                            (int) getScaledFrameWidth());
                    gameState.hitTower(Sprite.Player.LEFT, BOMB_DAMAGE);
               //     gameState.removeSoldier(this, true);
                }
            } else {
                if (getSoldierX() + getScaledFrameWidth() / 2 <=
                        gameState.getLeftTowerRightX()) {
                    super.attack(explosionPic, FIRE_FRAMES, FIRE_FPS);
                    super.setSoldierX(getSoldierX() +
                            (int) getScaledFrameWidth());
                    gameState.hitTower(Sprite.Player.RIGHT, BOMB_DAMAGE);
              //      gameState.removeSoldier(this, true);
                }
            }
        }
        super.update(gameTime);
    }

    public void render(Canvas canvas) {
        super.render(canvas);
    }

    public boolean isHitByArrow(Arrow arrow) {
        return super.isHitByArrow(arrow);
    }

    public static String info(){
        return "Damage: " + DAMAGE_PER_SEC + "\n" +
                "Bomb Damage: " + BOMB_DAMAGE + "\n" +
                "Nice and friendly grandpa. explodes once he reaches to the " +
                "opponent's tower.\n\n" +
                "Price: " + Market.BOMB_GRANDPA_BUY_PRICE;
    }
}