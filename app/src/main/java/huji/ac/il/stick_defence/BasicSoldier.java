package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * This class represents a basic soldier.
 */
public class BasicSoldier extends Soldier {

    //=======================BasicSoldier's abilities===========================
    private static final double SEC_TO_CROSS_SCREEN = 10;
    private static final int DAMAGE_PER_SEC = 1; // [Damage/Sec]
    private static final int HP = 10;
    //==========================================================================

    //==========================================================================
    //Soldier height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //soldier to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.150;
    //==========================================================================

    //============================Sprite constants==============================
    private static final int NUMBER_OF_FRAMES = 9;
    private static final int ATTACK_N_FRAMES = 11;
    private static final int MOVE_FPS = 40;
    private static final int ATTACK_FPS = 20;
    private static final int ATTACK_FRAME = 2;
    //==========================================================================

    private static Bitmap leftSoldierPic = null;
    private static Bitmap rightSoldierPic = null;
    private static Bitmap leftAttackSoldierPic = null;
    private static Bitmap rightAttackSoldierPic = null;

    private Sprite.Player player;

    /**
     * Constructor
     * @param context the context
     * @param player the player
     * @param delayInSec the delay from other player in seconds
     */
    public BasicSoldier(Context context, Sprite.Player player, double delayInSec) {
        super(context, player, SEC_TO_CROSS_SCREEN, DAMAGE_PER_SEC,
              Sounds.RUN_SOUND, delayInSec, HP, SoldierType.BASIC);
        if (null == leftSoldierPic) {
            leftSoldierPic = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.drawable.basic_soldier_run);
        }

        if (null == rightSoldierPic) {
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierPic);
        }

        if (null == leftAttackSoldierPic) {
            leftAttackSoldierPic =
                    BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.basic_soldier_hit);
        }

        if (null == rightAttackSoldierPic) {
            rightAttackSoldierPic = Sprite.mirrorBitmap(leftAttackSoldierPic);
        }

        if (Sprite.Player.LEFT == player) {
            super.initSprite(leftSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        } else {
            super.initSprite(rightSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        }
        this.player = player;
    }

    @Override
    public void update(long gameTime) {
        if (!super.isAttack()) {
            if (player == Sprite.Player.LEFT) {
                if (getSoldierX() + getScaledFrameWidth() / 2 >=
                        gameState.getRightTowerLeftX()) {
                    super.attack(leftAttackSoldierPic, ATTACK_N_FRAMES,
                            ATTACK_FPS);
                    super.setSoldierX(getSoldierX() + (int)
                            getScaledFrameWidth());
                    super.setSound(Sounds.BASIC_HIT);
                }
            } else {
                if (getSoldierX() + getScaledFrameWidth() / 2 <=
                        gameState.getLeftTowerRightX()) {
                    super.attack(rightAttackSoldierPic, ATTACK_N_FRAMES,
                            ATTACK_FPS);
                    super.setSoldierX(getSoldierX() + (int)
                            getScaledFrameWidth());
                    super.setSound(Sounds.BASIC_HIT);
                }
            }
        } else {
            if (getCurrentFrame() == ATTACK_FRAME){
                super.playSound();
            }
        }
        super.update(gameTime);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
    }

    @Override
    public boolean isHitByArrow(Arrow arrow) {
        return super.isHitByArrow(arrow);
    }

}