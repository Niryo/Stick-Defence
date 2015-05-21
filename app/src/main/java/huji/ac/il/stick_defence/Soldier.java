package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

/**
 * This class represents an abstract soldier figure.
 * Extension of this class must handle the transition to the attack mode
 * by itself. This can be handled by attack() and isAttack() methods.
 *
 */
public abstract class Soldier {

    protected GameState           gameState = GameState.getInstance();

    //Characteristics
    private final Sprite.Player   PLAYER;
    private final int             DAMAGE_PER_SEC;
    private final double          RUN_PIXELS_PER_SEC;

    //Soldier pictures
    private static Bitmap         leftSoldierPic = null;
    private static Bitmap         rightSoldierPic = null;
    private static Bitmap         leftAttackSoldierPic = null;
    private static Bitmap         rightAttackSoldierPic = null;
    private static int            HIT_EPSILON=15;
    private Sprite                sprite;

    //Positions
    private int                   screenWidth;
    private int                   screenHeight;
    private int                   soldierX;
    private int                   soldierY;
    private long                  lastUpdateTime;
    private boolean               attack;


    protected Soldier(Context context, Sprite.Player player, double screenWidthPerSec,
                      int damagePerSec) {
        this.PLAYER = player;

        this.screenWidth =
                context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;

        //Set x and speed
        if (player == Sprite.Player.LEFT) {
            this.RUN_PIXELS_PER_SEC = screenWidthPerSec * screenWidth;
            soldierX = 0;
        } else {
            this.RUN_PIXELS_PER_SEC = - screenWidthPerSec * screenWidth;
            soldierX = screenWidth;
        }
        this.DAMAGE_PER_SEC = damagePerSec;
        this.attack = false;
        lastUpdateTime = System.currentTimeMillis();
    }

    protected void initSprite(Context context, Bitmap soldierPic, int nFrames,
                              double screenPortion, int animationSpeed){

        sprite = new Sprite();
        sprite.initSprite(context, soldierPic, nFrames, PLAYER, screenPortion);
        sprite.setAnimationSpeed(animationSpeed);

        //set the y on the bottom of the screen
        this.soldierY = screenHeight - (int) sprite.getScaledFrameHeight();


    }

    protected void attack(Bitmap attackSoldierPic){
        this.attack = true;
        sprite.setPic(attackSoldierPic);
    }

    protected boolean isAttack(){
        return this.attack;
    }

    protected void update(long gameTime) {
        sprite.update(gameTime);
        double passedTimeInSec = (double)(gameTime - lastUpdateTime) / 1000;
        lastUpdateTime = gameTime;

        if (attack){
            gameState.hitTower(PLAYER, DAMAGE_PER_SEC * passedTimeInSec);
        } else {
            soldierX += (RUN_PIXELS_PER_SEC * passedTimeInSec);
        }
    }

    protected void render(Canvas canvas) {
        sprite.render(canvas, soldierX, soldierY);

        Paint paint=new Paint();
        paint.setColor(PLAYER == Sprite.Player.RIGHT ? Color.RED : Color.BLUE);
        paint.setStrokeWidth(10);
        canvas.drawLine(( float) (this.soldierX+(sprite.getScaledFrameWidth()/2) -HIT_EPSILON) ,this.soldierY, ( float) (this.soldierX+(sprite.getScaledFrameWidth()/2)+HIT_EPSILON) , this.soldierY, paint);

    }

    protected int getSoldierX(){
        return this.soldierX;
    }
    protected int getSoldierY(){
        return this.soldierY;
    }

    protected double getScaledFrameWidth(){
        return sprite.getScaledFrameWidth();
    }

    protected boolean checkHit(Arrow arrow){

        if (arrow.getPlayer() != this.getPlayer() &&
                this.soldierY <= arrow.getHeadY() &&
                Math.abs(this.soldierX +
                        sprite.getScaledFrameWidth() / 2 - arrow.getHeadX()) <=
                        HIT_EPSILON){
            Log.w("custom", "soldier hit!");
            return true;
        }

        return false;
    }

    public Sprite.Player getPlayer(){
        return this.PLAYER;
    }

}
