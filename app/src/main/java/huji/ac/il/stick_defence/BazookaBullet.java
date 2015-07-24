package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.Serializable;

public class BazookaBullet implements Serializable{
    //======================BazookaBullet's abilities===========================
    private static final double SEC_TO_SCREEN_WIDTH = 0.021;

    //========================BazookaBullet's picture===========================
    private static Sprite leftBulletSprite, rightBulletSprite;

    private GameState     gameState = GameState.getInstance();
    private float         x;
    private float         y;
    private int           screenWidth;
    private int           screenHeight;
    private long          lastUpdateTime;
    private double        x_pixPerSec;
    private Sprite.Player player;
    private Sprite        sprite;
    private static double scaleDownFactor;

    public BazookaBullet(Context context, float x, float y,
                         Sprite.Player player){
        this.screenWidth =
                context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;
        this.x=x;
        this.y=y;
        this.player = player;

        x_pixPerSec = SEC_TO_SCREEN_WIDTH * screenWidth;

        if (Sprite.Player.RIGHT == player){
            sprite = rightBulletSprite;
            x_pixPerSec *= -1;
        } else {
            sprite = leftBulletSprite;
        }

        resetUpdateTime();

    }

    public void update(long gameTime){
        double passedTimeInSec = (double)(gameTime - lastUpdateTime) / 1000;
        this.x += x_pixPerSec * passedTimeInSec;

        if (this.x <= gameState.getLeftTowerRightX() ||
            this.x >= gameState.getRightTowerLeftX()){
            gameState.removeBazookaBullet(this);
        }
    }

    public void resetUpdateTime(){
        lastUpdateTime = System.currentTimeMillis();
    }

    public void render(Canvas canvas){
        sprite.render(canvas, getHeadX(), getHeadY());

    }

    public static void init(Context context, double scaleDownFactor){

        Bitmap leftBazookaBulletPic = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bazooka_bullet); // Read resource only once
        Bitmap rightBazookaBulletPic = Sprite.mirrorBitmap(leftBazookaBulletPic);

        leftBulletSprite = new Sprite();
        leftBulletSprite.initSprite(context, leftBazookaBulletPic,
                1, Sprite.Player.LEFT, 1.0);
        leftBulletSprite.setScaleDownFactor(scaleDownFactor);

        rightBulletSprite = new Sprite();
        rightBulletSprite.initSprite(context, rightBazookaBulletPic,
                1, Sprite.Player.RIGHT, 1.0);
        rightBulletSprite.setScaleDownFactor(scaleDownFactor);

    }

    public int  getHeadX(){
        return (int) (this.x + sprite.getScaledFrameWidth()/2);
    }

    public int  getHeadY(){
        return (int) (this.y + sprite.getScaledFrameHeight()/2);
    }

    public Sprite.Player getPlayer(){
        return this.player;
    }

}
