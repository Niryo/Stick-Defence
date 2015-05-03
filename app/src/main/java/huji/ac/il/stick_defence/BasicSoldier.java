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

    private static Bitmap m_leftSoldierpic = null;
    private static Bitmap m_rightSoldierPic = null;
    private static Bitmap m_leftHitSoldirePic = null;
    private static Bitmap m_rightHitSoldierPic = null;

    private Sprite    m_sprite;
    private int       m_screenWidth;
    private int       m_screenHeight;
    private int       m_soldierX;
    private int       m_soldierY;
    private int       m_runSpeed; //todo: make the speed in pixels/seconds units.
    private GameState gameState = GameState.getInstance();

    public BasicSoldier(Context context, Sprite.Player player) {
        if  (null == m_leftSoldierpic) {
            m_leftSoldierpic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.simple_running_stick); // Read resource only once
        }

        if (null == m_rightSoldierPic){
            m_rightSoldierPic = Sprite.mirrorBitmap(m_leftSoldierpic);
        }

        if (null == m_leftHitSoldirePic){
            m_leftHitSoldirePic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.basic_soldier_hit);
        }

        if(null == m_rightHitSoldierPic){
            m_rightHitSoldierPic = Sprite.mirrorBitmap(m_leftHitSoldirePic);
        }

        m_sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            m_sprite.initSprite(context, m_leftSoldierpic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            m_sprite.initSprite(context, m_rightSoldierPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        m_sprite.setAnimationSpeed(ANIMATION_SPEED);

        this.m_screenWidth =
                context.getResources().getDisplayMetrics().widthPixels;
        this.m_screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;

        //set the y on the bottom of the screen
        this.m_soldierY = m_screenHeight - (int) m_sprite.getScaledFrameHeight();

        //Set x and speed
        if (player == Sprite.Player.LEFT) {
            m_runSpeed = RUN_SPEED;
            m_soldierX = 0;
        } else {
            m_runSpeed = -RUN_SPEED;
            m_soldierX = m_screenWidth;
        }
    }

    public void update(long gameTime) {
        m_sprite.update(gameTime);
        m_soldierX += m_runSpeed;
     /*   if (m_soldierX > m_screenWidth) {
            m_soldierX -= m_runSpeed;
            gameState.removeSoldier(this);
        }*/
        if (m_runSpeed > 0){
            if (m_soldierX + m_sprite.getScaledFrameWidth()/2 >= gameState.getRightTowerLeftX()){
                m_runSpeed = 0;
                m_sprite.setPic(m_leftHitSoldirePic);

            }
        }
        if (m_runSpeed < 0){
            if (m_soldierX + m_sprite.getScaledFrameWidth()/2 <= gameState.getLeftTowerRightX()){
                m_runSpeed = 0;
                m_sprite.setPic(m_rightHitSoldierPic);

            }
        }

    }

    public void render(Canvas canvas) {
        m_sprite.render(canvas, m_soldierX, m_soldierY);
    }


}