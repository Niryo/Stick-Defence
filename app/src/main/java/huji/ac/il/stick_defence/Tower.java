package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yahav on 28/04/15.
 */
public class Tower {

    private class Fire{
        private static final int NUMBER_OF_FRAMES = 4;
        private static final double FIRE_SCREEN_HEIGHT_PORTION = 0.1;
        private Sprite fireSprite;
        private int    fireX;
        private int    fireY;
        private Random random;

        public Fire(Context context){
            random = new Random();
            fireSprite = new Sprite();

            fireSprite.initSprite(context, firePic, NUMBER_OF_FRAMES, player,
                    FIRE_SCREEN_HEIGHT_PORTION);

            this.fireX =
                    random.nextInt((int)towerSprite.getScaledFrameWidth()
                                   - (int)fireSprite.getScaledFrameWidth()) +
                    towerX ;
            this.fireY =
                    random.nextInt((int)towerSprite.getScaledFrameHeight() -
                                   (int)fireSprite.getScaledFrameHeight()) +
                    towerY ;

        }

        public void update(long gameTime){
            fireSprite.update(gameTime);
        }

        public void render(Canvas canvas){
            fireSprite.render(canvas, fireX, fireY);
        }

    }

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.6;

    public static final double MAX_HP = 100.0;

    private static Bitmap   leftTowerPic = null;
    private static Bitmap   rightTowerPic = null;
    private static Bitmap   firePic = null;
    private Sprite          towerSprite;
    private ArrayList<Fire> fires;
    private Context         context;

    private double          hp;
    private int             screenWidth;
    private int             screenHeight;
    private Sprite.Player   player;
    private int             towerX;
    private int             towerY;

    private GameState gameState = GameState.getInstance();

    /**
     * Constructor
     *
     * @param context the context
     * @param player  the PLAYER - right or left
     */
    public Tower(Context context, Sprite.Player player) {
        if (null == leftTowerPic){
            leftTowerPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.blue_tower); // Read resource only once
        }
        if (null == rightTowerPic){
            rightTowerPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.red_tower); // Read resource only once
        }

        if (null == firePic){
            firePic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.fire);
        }

        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        towerSprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            towerSprite.initSprite(context, leftTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            towerX = 0;
        } else {
            towerSprite.initSprite(context, rightTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            towerX = screenWidth - (int) towerSprite.getScaledFrameWidth();
        }
        towerY = screenHeight - (int) towerSprite.getScaledFrameHeight();

        this.player = player;

        this.hp = MAX_HP; // TODO

        this.context = context;
        fires = new ArrayList<>();
    }

    /**
     * Updates tower's place and maybe picture
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        towerSprite.update(gameTime);
        for (Fire fire : fires){
            fire.update(gameTime);
        }
    }

    /**
     * Draws the tower
     *
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the fireSprite
        towerSprite.render(canvas, towerX, towerY);

        for (Fire fire : fires){
            fire.render(canvas);
        }
    }


    public double getWidth(){
        return this.towerSprite.getScaledFrameWidth();
    }
    public Sprite.Point getPosition(){
        return new Sprite.Point(this.towerX, this.towerY);
    }

    public int getLeftX() {return this.towerX; }

    public int getRightX() { return this.towerX +
                                    (int) towerSprite.getScaledFrameWidth(); }

    public int getCentralX(){ return this.towerX +
                                (int) towerSprite.getScaledFrameWidth() / 2;}

    /**
     * Reduces HP to the tower and handle the fire. Returns true iff there
     * is enough hp to reduce.
     * @param hp the hp amount to reduce
     * @return true iff there is enough hp to reduce
     */
    public boolean reduceHP(double hp){
        this.hp -= hp;

        if (this.hp < 0.75 * MAX_HP && this.fires.size() < 1){
            this.fires.add(new Fire(context));
        }
        if (this.hp < 0.5 * MAX_HP && this.fires.size() < 2){
            this.fires.add(new Fire(context));
        }
        if (this.hp < 0.25 * MAX_HP && this.fires.size() < 3){
            this.fires.add(new Fire(context));
        }
        if (this.hp < 0.1 * MAX_HP && this.fires.size() < 4){
            this.fires.add(new Fire(context));
        }
        gameState.setTowerProgressHP(this.hp, player);

        return this.hp > 0;
    }

}
