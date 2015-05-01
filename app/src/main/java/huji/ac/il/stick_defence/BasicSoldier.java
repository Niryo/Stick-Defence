package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BasicSoldier extends Sprite {

    //Tower height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //tower to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.166;

    private static final int NUMBER_OF_FRAMES = 9;
    private static final int ANIMATION_SPEED = 4;
    private static final int RUN_SPEED = 5;

    private static Bitmap m_leftSoldierpic = null;
    private static Bitmap m_rightSoldierPic = null;

    private int m_screenWidth;
    private int m_screenHeight;
    private int m_soldierX;
    private int m_soldierY;
    private int m_runSpeed; //todo: make the speed in pixels/seconds units.
    private GameState gameState = GameState.getInstance();

    public BasicSoldier(Context context, Player player) {
        if  (m_leftSoldierpic == null) {
            m_leftSoldierpic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.simple_running_stick); // Read resource only once
        }

        if (m_rightSoldierPic == null){
            m_rightSoldierPic = super.mirrorBitmap(m_leftSoldierpic);
        }

        if (player == Player.LEFT){
            super.initSprite(context, m_leftSoldierpic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            super.initSprite(context, m_rightSoldierPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        super.setAnimationSpeed(ANIMATION_SPEED);

        this.m_screenWidth =
                context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;

        //set the y on the bottom of the screen
        this.m_soldierY = m_screenHeight - (int) getScaledFrameHeight();

        //Set x and speed
        if (player == Player.LEFT) {
            m_runSpeed = RUN_SPEED;
            m_soldierX = 0;
        } else {
            m_runSpeed = -RUN_SPEED;
            m_soldierX = m_screenWidth;
        }
    }

    public void update(long gameTime) {
        super.update(gameTime);
        m_soldierX += m_runSpeed;
        if (m_soldierX > m_screenWidth) {
            m_soldierX -= m_runSpeed;
            gameState.removeSoldier(this);
        }
    }

    public void render(Canvas canvas) {
        super.render(canvas, m_soldierX, m_soldierY);
    }


}