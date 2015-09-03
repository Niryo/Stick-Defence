package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;

/**
 * This class represents the Arrow of the game
 */
public class Arrow implements Serializable {
    //===========================Arrow's abilities==============================
    private static final double SEC_TO_CROSS_SCREEN = 2;
    private static final int    ARROW_DAMAGE = 10;
    //============================Arrow's picture===============================
    private static Bitmap scaledArrowPic;
    private static Sprite sprite;
    //==========================================================================

    private GameState gameState = GameState.getInstance();
    private float x;
    private float y;
    private float degree;
    private float bm_offsetX;
    private float bm_offsetY;
    private transient Matrix matrix = new Matrix();
    private int screenWidth;
    private int screenHeight;
    private long lastUpdateTime;
    private double pixPerSec;
    private Sprite.Player player;

    /**
     * Constructor
     * @param x the x value
     * @param y the y value
     * @param tan arrow angle
     * @param player the player that shot
     * @param delayInSec
     */
    public Arrow(float x, float y, float[] tan,
                 Sprite.Player player, double delayInSec) {
        this.screenWidth =
                gameState.getCanvasWidth();
        Log.w("custom", "screen width:" + screenWidth);
        this.screenHeight =
                gameState.getCanvasHeight();
        this.x = x;
        this.y = y;


        this.player = player;
        this.bm_offsetX = scaledArrowPic.getWidth() / 2;
        this.bm_offsetY = scaledArrowPic.getHeight() / 2;
        this.degree = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        updateMatrix();
        double arrowHeight = screenHeight - this.y;
        double pathLength = (arrowHeight / (Math.cos(Math.toRadians(90 - this.degree))));
        double oldSpeed = ((double) screenWidth) / SEC_TO_CROSS_SCREEN;
        double pathLeft = pathLength - (oldSpeed * delayInSec);
        double timeLeft = pathLeft / oldSpeed;
        double newSpeed = pathLength / timeLeft;
        if (pathLeft > 0) {
            pixPerSec = newSpeed;
        } else {
            //todo:
            pixPerSec = oldSpeed;
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    private void updateMatrix() {
        matrix.reset();
        matrix.postRotate(this.degree, bm_offsetX, bm_offsetY);
        matrix.postTranslate(this.x - bm_offsetX, this.y - bm_offsetY);
    }

    public void update(long gameTime) {
        double passedTimeInSec = ((double) (gameTime - lastUpdateTime)) / 1000;
        lastUpdateTime = System.currentTimeMillis();
        this.x +=
                Math.cos(Math.toRadians(this.degree)) *
                        pixPerSec * passedTimeInSec;
        this.y +=
                Math.sin(Math.toRadians(this.degree)) *
                        pixPerSec * passedTimeInSec;
        updateMatrix();
        if (this.x > this.screenWidth || this.y > this.screenHeight) {
            gameState.removeArrow(this);
        }
    }

    public void render(Canvas canvas) {
        canvas.drawBitmap(scaledArrowPic, matrix, null);
        Paint paint = new Paint();
        paint.setColor(this.player ==
                Sprite.Player.RIGHT ? Color.RED : Color.BLUE);
        paint.setStrokeWidth(10);
        canvas.drawPoint(this.getHeadX(), this.getHeadY(), paint);
    }

    public static void init(Context context, double scaleDownFactor) {
        Bitmap arrowPic = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.arrow); // Read resource only once

        if (sprite == null) {
            sprite = new Sprite();
            sprite.initSprite(context, arrowPic, 1, Sprite.Player.LEFT, 1.0);
            sprite.setScaleDownFactor(scaleDownFactor);
        }

        if (scaledArrowPic == null) {
            scaledArrowPic =
                    Bitmap.createScaledBitmap(arrowPic,
                            (int) sprite.getScaledFrameWidth(),
                            (int) sprite.getScaledFrameHeight(), false);
        }
    }

    public int getHeadX() {
        return (int) (this.x + Math.cos(Math.toRadians(this.degree))
                * scaledArrowPic.getWidth() / 2);
    }

    public int getHeadY() {
        return (int) (this.y + Math.sin(Math.toRadians(this.degree))
                * scaledArrowPic.getWidth() / 2);
    }

    public Sprite.Player getPlayer() {
        return this.player;
    }

    public int getArrowDamage(){ return ARROW_DAMAGE; }
}
