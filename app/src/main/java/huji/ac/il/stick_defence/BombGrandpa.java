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
    private static final int MOVE_FPS = 40;
    //==========================================================================

    private static Bitmap leftSoldierPic = null;
    private static Bitmap rightSoldierPic = null;
    private static Sounds sounds;

    private Sprite.Player player;

    /**
     * Constructor
     * @param context the context
     * @param player the player
     * @param delayInSec the delay from other player in seconds
     */
    public BombGrandpa(Context context, Sprite.Player player, double delayInSec){
        super(context, player, SEC_TO_CROSS_SCREEN, DAMAGE_PER_SEC,
                Sounds.OLD_MAN_SOUND, delayInSec, HP, SoldierType.BOMB_GRANDPA);

        if (null == leftSoldierPic) {
            leftSoldierPic = BitmapFactory.decodeResource(
                    context.getResources(),
                    R.drawable.bomb_grandpa);
        }

        if (null == rightSoldierPic) {
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierPic);
        }
        
        if (Sprite.Player.LEFT == player) {
            super.initSprite(context, leftSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        } else {
            super.initSprite(context, rightSoldierPic, NUMBER_OF_FRAMES,
                    SCREEN_HEIGHT_PORTION, MOVE_FPS);
        }
        this.player = player;
        this.sounds = Sounds.getInstance();
    }

    @Override
    public void update(long gameTime) {
        if (!super.isAttack()) {
            if (player == Sprite.Player.LEFT) {
                if (getSoldierX() + getScaledFrameWidth() / 2 >=
                        gameState.getRightTowerLeftX()) {
                    gameState.hitTower(Sprite.Player.LEFT, BOMB_DAMAGE);
                    gameState.removeSoldier(this, true);
                }
            } else {
                if (getSoldierX() + getScaledFrameWidth() / 2 <=
                        gameState.getLeftTowerRightX()) {
                    gameState.hitTower(Sprite.Player.RIGHT, BOMB_DAMAGE);
                    gameState.removeSoldier(this, true);
                }
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

    @Override
    public void playSound(){
        soundStream = sounds.playSound(soundId,false);
    }

    public static String info(){
        return "Damage: " + DAMAGE_PER_SEC + "\n" +
                "Bomb Damage: " + BOMB_DAMAGE + "\n" +
                "HP: " + HP + "\n" +
                "Nice and friendly grandpa. explodes once he reaches to the " +
                "opponent's tower.\n\n" +
                "Price: " + Market.BOMB_GRANDPA_BUY_PRICE;
    }
}