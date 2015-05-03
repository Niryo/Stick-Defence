package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BasicSoldier {

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.166;

    private static final int NUMBER_OF_FRAMES = 9;
    private static final int ANIMATION_SPEED = 4;
    private static final int RUN_SPEED = 5;

    private static Bitmap leftSoldierpic = null;
    private static Bitmap rightSoldierPic = null;
    private static Bitmap leftHitSoldirePic = null;
    private static Bitmap rightHitSoldierPic = null;

    private Sprite    sprite;
    private int       screenWidth;
    private int       screenHeight;
    private int       soldierX;
    private int       soldierY;
    private int       runSpeed; //todo: make the speed in pixels/seconds units.
    private GameState gameState = GameState.getInstance();

    public BasicSoldier(Context context, Sprite.Player player) {
        if  (null == leftSoldierpic) {
            leftSoldierpic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.simple_running_stick); // Read resource only once
        }

        if (null == rightSoldierPic){
            rightSoldierPic = Sprite.mirrorBitmap(leftSoldierpic);
        }

        if (null == leftHitSoldirePic){
            leftHitSoldirePic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.basic_soldier_hit);
        }

        if(null == rightHitSoldierPic){
            rightHitSoldierPic = Sprite.mirrorBitmap(leftHitSoldirePic);
        }

        sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            sprite.initSprite(context, leftSoldierpic, NUMBER_OF_FRAMES,
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
            runSpeed = RUN_SPEED;
            soldierX = 0;
        } else {
            runSpeed = -RUN_SPEED;
            soldierX = screenWidth;
        }
    }

    public void update(long gameTime) {
        sprite.update(gameTime);
        soldierX += runSpeed;
     /*   if (soldierX > screenWidth) {
            soldierX -= runSpeed;
            gameState.removeSoldier(this);
        }*/
        if (runSpeed > 0){
            if (soldierX + sprite.getScaledFrameWidth()/2 >= gameState.getRightTowerLeftX()){
                runSpeed = 0;
                sprite.setPic(leftHitSoldirePic);

            }
        }
        if (runSpeed < 0){
            if (soldierX + sprite.getScaledFrameWidth()/2 <= gameState.getLeftTowerRightX()){
                runSpeed = 0;
                sprite.setPic(rightHitSoldierPic);

            }
        }

    }

    public void render(Canvas canvas) {
        sprite.render(canvas, soldierX, soldierY);
    }


}