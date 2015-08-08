package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;

/**
 * This class represents an abstract soldier figure.
 * Extension of this class must handle the transition to the attack mode
 * by itself. This can be handled by attack() and isAttack() methods.
 */
public abstract class Soldier implements Serializable{
    //Epsilon to hit soldier from his center
    private static final int HIT_EPSILON = 15;

    protected GameState gameState = GameState.getInstance();

    //Soldier pictures
    private static Bitmap leftSoldierPic = null;
    private static Bitmap rightSoldierPic = null;
    private static Bitmap leftAttackSoldierPic = null;
    private static Bitmap rightAttackSoldierPic = null;
    private Sprite        sprite;

    //Characteristics
    private final Sprite.Player PLAYER;
    private final int           DAMAGE_PER_SEC;
    private double              runPixelsPerSec;

    //Positions
    private int      screenWidth;
    private int      screenHeight;
    private double   soldierX;
    private double   soldierY;
    private long     lastUpdateTime;
    private boolean  attack;
    private double   secToCrossScreen;
    private double   delayInSec;
    private Context  context;


    protected Soldier(Context context, Sprite.Player player, double
            secToCrossScreen, int damagePerSec, double delayInSec) {
        this.PLAYER = player;

        this.screenWidth = gameState.getCanvasWidth();

        this.screenHeight = gameState.getCanvasHeight();
        this.context = context;

        this.DAMAGE_PER_SEC = damagePerSec;
        this.attack = false;
        lastUpdateTime = System.currentTimeMillis();
        this.delayInSec = delayInSec;
        this.secToCrossScreen = secToCrossScreen;
    }

    protected void initSprite(Context context, Bitmap soldierPic, int
            nFrames, double screenPortion, int animationSpeed) {

        sprite = new Sprite();
        sprite.initSprite(context, soldierPic, nFrames, PLAYER, screenPortion);
        sprite.setAnimationSpeed(animationSpeed);

        //set the y on the bottom of the screen
        this.soldierY = screenHeight - (int) sprite.getScaledFrameHeight();

        //Set x
        if (this.PLAYER == Sprite.Player.LEFT) {
            soldierX = - sprite.getScaledFrameWidth(); //Start hidden
        } else {
            soldierX = screenWidth; //Start hidden
        }

        //Set speed
        if (this.PLAYER == Sprite.Player.LEFT) {
            this.runPixelsPerSec = ((double) screenWidth ) / (secToCrossScreen - delayInSec);

        } else {
            this.runPixelsPerSec = - ((double) screenWidth ) / (secToCrossScreen - delayInSec);
        }
        Log.w("custom", "Soldier pix per sec: "+ runPixelsPerSec);

    }

    protected Context getContext(){ return this.context; }

    protected void attack(Bitmap attackSoldierPic, int nFrames, int fps) {
        this.attack = true;
        sprite.setPic(attackSoldierPic, nFrames);
        sprite.setAnimationSpeed(fps);
        this.soldierY = screenHeight - (int) sprite.getScaledFrameHeight();
    }

    protected double getScaledDownFactor(){ return sprite.getScaleDownFactor();}

    protected boolean isAttack() { return this.attack; }

    protected void update(long gameTime) {
        sprite.update(gameTime);
        double passedTimeInSec = (double) (gameTime - lastUpdateTime) / 1000;
        lastUpdateTime = System.currentTimeMillis();

        if (attack) {
            gameState.hitTower(PLAYER, DAMAGE_PER_SEC * passedTimeInSec);
        } else {
            soldierX += (runPixelsPerSec * passedTimeInSec);
        }

        //Log.w("custom", "run: "+ runPixelsPerSec+" current:"+
        // (runPixelsPerSec * passedTimeInSec) );
    }



    protected void render(Canvas canvas) {
        sprite.render(canvas, getSoldierX(), getSoldierY());

        Paint paint = new Paint();
        paint.setColor(PLAYER == Sprite.Player.RIGHT ? Color.RED : Color.BLUE);
        paint.setStrokeWidth(10);
        canvas.drawLine((float) (getSoldierX() + (sprite.getScaledFrameWidth
                () / 2) - HIT_EPSILON), getSoldierY(), (float) (this.soldierX
                + (sprite.getScaledFrameWidth() / 2) + HIT_EPSILON),
                getSoldierY(), paint);

    }

    protected int getSoldierX() { return (int) Math.round(this.soldierX); }

    protected void setSoldierX(int soldierX) { this.soldierX = soldierX; }

    protected int getSoldierY() { return (int) Math.round(this.soldierY); }

    protected double getScaledFrameWidth() {
        return sprite.getScaledFrameWidth();
    }

    protected boolean isHitByArrow(Arrow arrow) {

        if (arrow.getPlayer() != this.getPlayer() &&
                this.soldierY <= arrow.getHeadY() &&
                Math.abs(this.soldierX + sprite.getScaledFrameWidth() / 2 -
                        arrow.getHeadX()) <= HIT_EPSILON) {
            Log.w("custom", "soldier hit!");
            return true;
        }

        return false;
    }

    public Sprite.Player getPlayer() {
        return this.PLAYER;
    }

    public int getScreenWidth(){ return this.screenWidth; }

    public int getCurrentFrame(){ return sprite.getCurrentFrame(); }
}
