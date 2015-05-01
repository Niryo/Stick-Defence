package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by yahav on 01/05/15.
 */
public class Bow extends Sprite{
    //Bow height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //bow to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.15;
    private static final int NUMBER_OF_FRAMES = 9;

    Point m_strechBegin = null;
    Point m_strechEnd = null;
    private static        Bitmap m_leftBowPic = null;
    private static        Bitmap m_rightBowPic = null;
    private double        m_frameScaledWidth;   //the frame width after scale
    private double        m_frameScaledHeight;  //the frame height after scale
    private double        m_bowAngle;
    private int           m_screenWidth;
    private int           m_screenHeight;
    private int           m_towerHeight;
    private Sprite.Player m_player;

    /**
     * Constructor
     * @param context the context
     * @param player the player - right or left
     */
    public Bow(Context context, Sprite.Player player, int towerHeight) {
        if (null == m_leftBowPic){
            m_leftBowPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bow); // Read resource only once
        }
        if (null == m_rightBowPic){
            m_rightBowPic = super.mirrorBitmap(m_leftBowPic);
        }

        if (player == Player.LEFT){
            super.initSprite(context, m_leftBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            super.initSprite(context, m_rightBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        this.m_player = player;

        double frameHeight = m_leftBowPic.getHeight();
        double frameWidth = m_leftBowPic.getWidth() / NUMBER_OF_FRAMES;
        this.m_screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        double scaleDownFactor = super.getScaleDownFactor();

        this.m_frameScaledHeight = frameHeight/scaleDownFactor;
        this.m_frameScaledWidth = frameWidth/scaleDownFactor;

        this.m_towerHeight = towerHeight;

        this.m_strechBegin = new Point();
        this.m_strechEnd = new Point();

    }

    public void startStrech(float x, float y){
        m_strechBegin.set(x, y);
    }

    public void strech(float x, float y){
        m_strechEnd.set(x, y);
        m_bowAngle = Math.toDegrees(
                     Math.atan2(m_strechBegin.getY() - m_strechEnd.getY(),
                                m_strechBegin.getX() - m_strechEnd.getX()));
        Log.w("yahav", String.valueOf(m_bowAngle));


    }

    public void release(){
        //TODO - shoot an arrow
        m_strechBegin.invalidate();
        m_strechEnd.invalidate();
    }

    /**
     * Updates bow's place and angel
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        super.update(gameTime);

    }

    /**
     * Draws the tower
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        if (m_player == Sprite.Player.RIGHT){
            super.render(canvas, (int)(m_screenWidth - m_frameScaledWidth),
                    (int)(m_towerHeight - m_frameScaledHeight));
        } else {
            super.render(canvas, 0, (int)(m_towerHeight - m_frameScaledHeight));
        }
    }
}
