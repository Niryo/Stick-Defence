package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yahav on 28/04/15.
 */
public class Tower {

    private class Fire{
        private static final int NUMBER_OF_FRAMES = 4;
        private static final double FIRE_SCREEN_HEIGHT_PORTION = 0.1;
        private Sprite sprite;
        private int    screenWidth;
        private int    screenHeight;

        public Fire(Context context){
            sprite = new Sprite();

            sprite.initSprite(context, firePic, NUMBER_OF_FRAMES, player,
                              FIRE_SCREEN_HEIGHT_PORTION);

        }

        public void update(long gameTime){
            sprite.update(gameTime);
        }

        public void render(Canvas canvas){
            sprite.render(canvas, towerX, towerY);
        }

    }

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.6;

    private static Bitmap   leftTowerPic = null;
    private static Bitmap   rightTowerPic = null;
    private static Bitmap   firePic = null;
    private Sprite          sprite;
    private ArrayList<Fire> fires;
    private Context         context;

    private double          hp;
    private int             screenWidth;
    private int             screenHeight;
    private Sprite.Player   player;
    private int             towerX;
    private int             towerY;

    /**
     * Constructor
     *
     * @param context the context
     * @param player  the player - right or left
     */
    public Tower(Context context, Sprite.Player player) {
        if (null == leftTowerPic){
            leftTowerPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.tower); // Read resource only once
        }
        if (null == rightTowerPic){
            rightTowerPic = Sprite.mirrorBitmap(leftTowerPic);
        }

        if (null == firePic){
            firePic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.fire);
        }

        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            sprite.initSprite(context, leftTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            towerX = 0;
        } else {
            sprite.initSprite(context, rightTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            towerX = screenWidth - (int)sprite.getScaledFrameWidth();
        }
        towerY = screenHeight - (int)sprite.getScaledFrameHeight();

        this.player = player;

        this.hp = 100.0; // TODO

        this.context = context;
        fires = new ArrayList<>();
    }

    /**
     * Updates tower's place and maybe picture
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        sprite.update(gameTime);
        for (Fire fire : fires){
            fire.update(gameTime);
        }
        //TODO - Consider change the picture when tower is damaged
    }

    /**
     * Draws the tower
     *
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        sprite.render(canvas, towerX, towerY);

        for (Fire fire : fires){
            fire.render(canvas);
        }
    }

    public int getTowerHeight(){
        return (int) (screenHeight - sprite.getScaledFrameHeight());
    }

    public int getLeftX() {return this.towerX; }

    public int getRightX() { return this.towerX + (int) sprite.getScaledFrameWidth(); }

    public void reduceHP(int hp){
        this.hp -= hp;

        Log.w("yahav", String.valueOf(hp));
        if (this.hp < 75 && this.fires.size() < 1){
            this.fires.add(new Fire(context));
        }

    }

}
