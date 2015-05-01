package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
/**
 * Created by yahav on 28/04/15.
 */
public class Tower extends Sprite{

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.5;

    private static Bitmap m_leftTowerPic = null;
    private static Bitmap m_rightTowerPic = null;
    private double        m_frameScaledWidth;   //the frame width after scale
    private double        m_frameScaledHeight;  //the frame height after scale
    private double        m_hp;
    private int           m_screenWidth;
    private int           m_screenHeight;
    private Player        m_player;

    /**
     * Constructor
     * @param context the context
     * @param player the player - right or left
     */
    public Tower(Context context, Player player) {
        // Read resource only once
        if (null == m_leftTowerPic){
            m_leftTowerPic =
                    BitmapFactory.decodeResource(context.getResources(),
                                                 R.drawable.tower);
        }
        if (null == m_rightTowerPic){
            m_rightTowerPic = super.mirrorBitmap(m_leftTowerPic);
        }

        if (player == Player.LEFT){
            super.initSprite(context, m_leftTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
        } else {
            super.initSprite(context, m_rightTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
        }

        this.m_player = player;

        double frameHeight = m_leftTowerPic.getHeight();
        double frameWidth = m_leftTowerPic.getWidth();
        this.m_screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        double scaleDownFactor = super.getScaleDownFactor();

        this.m_frameScaledHeight = frameHeight/scaleDownFactor;
        this.m_frameScaledWidth = frameWidth/scaleDownFactor;

        this.m_hp = 100.0; // TODO
    }

    /**
     * Updates tower's place and maybe picture
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        super.update(gameTime);
        //TODO - Consider change the picture when tower is damaged
    }

    /**
     * Draws the tower
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        if (m_player == Player.RIGHT){
            super.render(canvas, (int)(m_screenWidth - m_frameScaledWidth),
                    (int)(m_screenHeight - m_frameScaledHeight));
        } else {
            super.render(canvas, 0, (int)(m_screenHeight - m_frameScaledHeight));
        }
    }

    /**
     * Get the tower height
     * @return tower height
     */
    public int getTowerHeight(){
        return (int) (m_screenHeight - m_frameScaledHeight);
    }
}