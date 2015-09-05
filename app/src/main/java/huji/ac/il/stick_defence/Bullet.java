package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


/**
 * This class represents a bullet
 */
public class Bullet {
    //==========================Bullet's abilities==============================
    private static final double SEC_TO_SCREEN_WIDTH = 0.021;
    //===========================Bullet's picture===============================
    private static Sprite leftBulletSprite, rightBulletSprite;
    private static Bitmap leftBazookaBulletPic, rightBazookaBulletPic;
    private GameState gameState = GameState.getInstance();
    private float x, y;
    private long lastUpdateTime;
    private double x_pixPerSec;
    private Sprite sprite;

    /**
     * Constructor
     * @param x the x value
     * @param y the y value
     * @param player the player who shot the bullet
     */
    public Bullet(float x, float y, Sprite.Player player) {
        this.x = x;
        this.y = y;

        x_pixPerSec = SEC_TO_SCREEN_WIDTH * GameState.getCanvasWidth();

        if (Sprite.Player.RIGHT == player) {
            sprite = rightBulletSprite;
            x_pixPerSec *= -1;
        } else {
            sprite = leftBulletSprite;
        }
        resetUpdateTime();
    }

    /**
     * Update the bullet position
     * @param gameTime the current time
     */
    public void update(long gameTime) {
        double passedTimeInSec = (double) (gameTime - lastUpdateTime) / 1000;
        this.x += x_pixPerSec * passedTimeInSec;

        if (this.x <= gameState.getLeftTowerCentralX() ||
                this.x >= gameState.getRightTowerCentralX()) {
            playExplosionSound();
            gameState.removeBullet(this);
        }
    }


    private void resetUpdateTime() {
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Draw the bullet
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        sprite.render(canvas, getHeadX(), getHeadY());
    }

    /**
     * Init the bullet. must be called after construct
     * @param context the context
     * @param scaleDownFactor the scale down factor
     */
    public static void init(Context context, double scaleDownFactor) {
        if (null == leftBazookaBulletPic){
            leftBazookaBulletPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bazooka_bullet); // Read resource only once
        }

        if (null == rightBazookaBulletPic){
            rightBazookaBulletPic = Sprite.mirrorBitmap(leftBazookaBulletPic);
        }

        leftBulletSprite = new Sprite();
        leftBulletSprite.initSprite(leftBazookaBulletPic, 1,
                Sprite.Player.LEFT, 1.0);
        leftBulletSprite.setScaleDownFactor(scaleDownFactor);

        rightBulletSprite = new Sprite();
        rightBulletSprite.initSprite(rightBazookaBulletPic, 1,
                Sprite.Player.RIGHT, 1.0);
        rightBulletSprite.setScaleDownFactor(scaleDownFactor);

    }

    private int getHeadX() {
        return (int) (this.x + sprite.getScaledFrameWidth() / 2);
    }

    private int getHeadY() {
        return (int) (this.y + sprite.getScaledFrameHeight() / 2);
    }

    private void playExplosionSound(){
        Sounds.playSound(Sounds.SMALL_EXPLOSION, false);
    }

}
