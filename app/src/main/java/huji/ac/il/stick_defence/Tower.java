package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by yahav on 28/04/15.
 */
public class Tower {

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.6;

    private static Bitmap m_leftTowerPic = null;
    private static Bitmap m_rightTowerPic = null;
    private Sprite        m_sprite;

    private double        m_hp;
    private int           m_screenWidth;
    private int           m_screenHeight;
    private Sprite.Player m_player;
    private int           m_towerX;
    private int           m_towerY;

    /**
     * Constructor
     *
     * @param context the context
     * @param player  the player - right or left
     */
    public Tower(Context context, Sprite.Player player) {
        if (null == m_leftTowerPic){
            m_leftTowerPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.tower); // Read resource only once
        }
        if (null == m_rightTowerPic){
            m_rightTowerPic = Sprite.mirrorBitmap(m_leftTowerPic);
        }

        this.m_screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        m_sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            m_sprite.initSprite(context, m_leftTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            m_towerX = 0;
        } else {
            m_sprite.initSprite(context, m_rightTowerPic, 1, player, SCREEN_HEIGHT_PORTION);
            m_towerX = m_screenWidth - (int)m_sprite.getScaledFrameWidth();
        }
        m_towerY = m_screenHeight - (int)m_sprite.getScaledFrameHeight();

        this.m_player = player;

        this.m_hp = 100.0; // TODO
    }

    /**
     * Updates tower's place and maybe picture
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        m_sprite.update(gameTime);
        //TODO - Consider change the picture when tower is damaged
    }

    /**
     * Draws the tower
     *
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        m_sprite.render(canvas, m_towerX, m_towerY);
    }

    public int getTowerHeight(){
        return (int) (m_screenHeight - m_sprite.getScaledFrameHeight());
    }

    public int getLeftX() {return this.m_towerX; }

    public int getRightX() { return this.m_towerX + (int) m_sprite.getScaledFrameWidth(); }

}
