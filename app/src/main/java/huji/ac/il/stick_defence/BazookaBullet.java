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
    private static Bitmap scaledBazookaBulletPic;
    private static Sprite sprite;

    private GameState     gameState = GameState.getInstance();
    private float         x;
    private float         y;
    private int           screenWidth;
    private int           screenHeight;
    private long          lastUpdateTime;
    private double        x_pixPerSec;
    private Sprite.Player player;

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
            x_pixPerSec *= -1;
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

        Bitmap bazookaArrowPic = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bazooka_bullet); // Read resource only once

        if(sprite==null){
            sprite= new Sprite();
            sprite.initSprite(context, bazookaArrowPic, 1, Sprite.Player.LEFT, 1.0);
            sprite.setScaleDownFactor(scaleDownFactor);
        }

        if(scaledBazookaBulletPic==null){
            scaledBazookaBulletPic =
                    Bitmap.createScaledBitmap(bazookaArrowPic,
                            (int) sprite.getScaledFrameWidth(),
                            (int) sprite.getScaledFrameHeight(), false);
        }
    }

    public int  getHeadX(){
        return (int) (this.x + scaledBazookaBulletPic.getWidth()/2);
    }

    public int  getHeadY(){
        return (int) (this.y + scaledBazookaBulletPic.getWidth()/2);
    }

    public Sprite.Player getPlayer(){
        return this.player;
    }

}
