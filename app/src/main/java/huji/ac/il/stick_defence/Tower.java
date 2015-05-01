package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by yahav on 28/04/15.
 */
public class Tower extends Sprite {

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.6;

    private static Bitmap m_towerPic = null;
    private double m_hp;
    private int m_screenWidth;
    private int m_screenHeight;
    private Player m_player;

    /**
     * Constructor
     *
     * @param context the context
     * @param player  the player - right or left
     */
    public Tower(Context context, Player player) {
        if (null == m_towerPic) {
            m_towerPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.tower); // Read resource only once
        }
        super.initSprite(context, m_towerPic, 1, player, SCREEN_HEIGHT_PORTION);
        this.m_player = player;
        this.m_screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.m_hp = 100.0; // TODO
    }

    /**
     * Updates tower's place and maybe picture
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        super.update(gameTime);
        //TODO - Consider change the picture when tower is damaged
    }

    /**
     * Draws the tower
     *
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        if (m_player == Player.RIGHT) {
            super.render(canvas, (int) (m_screenWidth - super.getScaledFrameWidth()),
                    (int) (m_screenHeight - super.getScaledFrameHeight()));
        } else {
            super.render(canvas, 0, (int) (m_screenHeight - super.getScaledFrameHeight()));
        }
    }
}
