package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class BasicSoldier {

    //=======================BasicSoldier's abilities===========================
    private static final double RUN_PIXELS_PER_SEC = 200; // [Pixels/Sec]
    private static final int    DAMAGE_PIXELS_PER_SEC = 1; // [Damage/Sec]
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


    private static Bitmap leftSoldierPic = null;
    private static Bitmap rightSoldierPic = null;
    private static Bitmap leftHitSoldierPic = null;
    private static Bitmap rightHitSoldierPic = null;

    private Sprite        sprite;
    private int           screenWidth;
    private int           screenHeight;
    private int           soldierX;
    private int           soldierY;
    private double        runPixelsPerSec;
    private GameState     gameState = GameState.getInstance();
    private Sprite.Player player;
    private long          lastUpdateTime;

    public BasicSoldier(Context context, Sprite.Player player) {
        if  (null == leftSoldierPic) {
            leftSoldierPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.basic_soldier_run); // Read resource only once
        }

        if (null == rightSoldierPic){
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierPic);
        }

        if (null == leftHitSoldierPic){
            leftHitSoldierPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.basic_soldier_hit);
        }

        if(null == rightHitSoldierPic){
            rightHitSoldierPic = Sprite.mirrorBitmap(leftHitSoldierPic);
        }

        this.player = player;

        sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            sprite.initSprite(context, leftSoldierPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            sprite.initSprite(context, rightSoldierPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        sprite.setAnimationSpeed(ANIMATION_SPEED);

        this.screenWidth =
                context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;

        //set the y on the bottom of the screen
        this.soldierY = screenHeight - (int) sprite.getScaledFrameHeight();

        //Set x and speed
        if (player == Sprite.Player.LEFT) {
            runPixelsPerSec = RUN_PIXELS_PER_SEC;
            soldierX = 0;
        } else {
            runPixelsPerSec = -RUN_PIXELS_PER_SEC;
            soldierX = screenWidth;
        }

        lastUpdateTime = System.currentTimeMillis();


    }

    public void update(long gameTime) {
        sprite.update(gameTime);
        double passedTimeInSec = (double)(gameTime - lastUpdateTime) / 1000;
        lastUpdateTime = gameTime;
        soldierX += (runPixelsPerSec * passedTimeInSec);

        if (runPixelsPerSec > 0){
            if (soldierX + sprite.getScaledFrameWidth() / 2 >=
                    gameState.getRightTowerLeftX()){
                runPixelsPerSec = 0;
                sprite.setPic(leftHitSoldierPic);

            }
        }
        if (runPixelsPerSec < 0){
            if (soldierX + sprite.getScaledFrameWidth()/2 <=
                    gameState.getLeftTowerRightX()){
                runPixelsPerSec = 0;
                sprite.setPic(rightHitSoldierPic);

            }
        }
        if (runPixelsPerSec == 0){
            if (player == Sprite.Player.LEFT){
                gameState.hitTower(Sprite.Player.RIGHT,
                                   DAMAGE_PIXELS_PER_SEC * passedTimeInSec);
            } else {
                gameState.hitTower(Sprite.Player.LEFT,
                                   DAMAGE_PIXELS_PER_SEC * passedTimeInSec);
            }
        }
    }

    public void render(Canvas canvas) {
        sprite.render(canvas, soldierX, soldierY);
    }

    public boolean checkHit(Arrow arrow){
        int tempx= arrow.getHeadX();
        int tempy=arrow.getHeadY();
        int tempsol= (int) (this.soldierX+sprite.getScaledFrameWidth()/2);

        if((int) this.soldierX+sprite.getScaledFrameWidth()/2 == arrow.getHeadX() && this.soldierY<= arrow.getHeadY()){ //todo: fix.
            //todo: hit
            Log.w("custom", "soldier hit!");
            return true;
        }
        return false;
    }

}