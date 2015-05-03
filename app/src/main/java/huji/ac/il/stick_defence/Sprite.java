package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

/**
 * This class represents a sprite animation object
 */
public class Sprite{
    /**
     * Represents left or right player
     */
    public enum Player{
        LEFT,
        RIGHT
    }

    private Bitmap  m_bitmap;
    private Rect    m_frameRect;  // the rectangle to be drawn from the animation bitmap
    private RectF   m_destRect;
    private int     m_frameHeight;
    private int     m_frameWidth;
    private int     m_currentFrame = 0;    // the current frame

    private int     m_nFrames = 9;        // number of frames in animation
    private int     m_fps = 4;             //the speed of the animation
    private int     m_framePeriod
            = 100 / m_fps;    // milliseconds between each frame (1000/m_fps)
    private long    m_frameTicker = 01;    // the time of the last frame update
    private double  m_scaleDownFactor;
    private Player  m_player;
    private float   m_angle;
    private float   m_screenHeightPortion;

    /**
     * Default Constructor
     */
    public Sprite(){}

    /**
     * Initialize the sprite. Must be called after construct.
     *
     * @param context the context
     * @param bitmap the bitmap of the sprite
     * @param nFrames the number of frames in the sprite
     * @param player left or right player
     * @param screenHeightPortion sprite height in relation to the screen height.
     *                            0-1 double. For instance, 0.5 will cause the
     *                            sprite to span over a half of the screen height.
     */
    public void initSprite(Context context, Bitmap bitmap, int nFrames,
                              Player player, double screenHeightPortion){

        this.m_bitmap = bitmap;

        this.m_player = player;
        this.m_nFrames = nFrames;
        this.m_frameHeight = bitmap.getHeight();
        this.m_frameWidth = (bitmap.getWidth() / nFrames);
        this.m_frameRect = new Rect(0, 0, m_frameWidth, m_frameHeight);
        this.m_destRect = new RectF();
        int screenHeight =
                context.getResources().getDisplayMetrics().heightPixels;
        setScaleDownFactor(((double) this.m_frameHeight / (double) screenHeight)
                / screenHeightPortion);
        this.m_angle = 20;
        m_screenHeightPortion = (float)screenHeightPortion;
    }

    /**
     * Mirror bitmap. Release the old bitmap to save space.
     *
     * @param src the bitmap to mirror
     * @return a mirrored bitmap
     */
    public static Bitmap mirrorBitmap(Bitmap src){
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    /**
     * Sets the animation speed (fps)
     *
     * @param fps
     */
    public void setAnimationSpeed(int fps){
        this.m_fps = fps;
    }

    /**
     * Update the sprite. if enough time as been passed since the last update, the
     * sprite will load the next frame.
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        if (gameTime > m_frameTicker + m_framePeriod) {
            m_frameTicker = gameTime;

            if (m_nFrames > 1){
                // increment the frame
                if (m_player == Player.LEFT){
                    m_currentFrame++;
                    if (m_currentFrame >= m_nFrames) {
                        m_currentFrame = 0;
                    }
                } else {
                    m_currentFrame--;
                    if (m_currentFrame < 0){
                        m_currentFrame = m_nFrames - 1;
                    }
                }
            }

        }

        this.m_frameRect.left = this.m_currentFrame * m_frameWidth;
        this.m_frameRect.right = this.m_frameRect.left + m_frameWidth;
    }

    /**
     * Draws the sprite on the canvas in the given point. the sprite will be drawn from the
     * top left.
     *
     * @param canvas the canvas
     * @param x the x axis
     * @param y the y axis
     */
    public void render(Canvas canvas, int x, int y){
        // where to draw the sprite
        m_destRect.set(x, y, (x + (int)getScaledFrameWidth()), (y + (int)getScaledFrameHeight()));

        canvas.drawBitmap(this.m_bitmap, this.m_frameRect, m_destRect, null);
    }

    public Rect getFrameRect(){
        return this.m_frameRect;
    }
    public RectF getDestRect(){
        return this.m_destRect;
    }

    /**
     * Set the scale factor of the bitmap. if the bitmap is big and you want it to appear small, set
     * a high scale factor.
     * @param scale the amount of scale
     */
    public void setScaleDownFactor(double scale){
        this.m_scaleDownFactor = scale;
    }

    /**
     * Returns the scan down factor, that is a factor that shrink or enlarge
     * the sprite in relation to the screen dimensions.
     * @return m_scaleDownFactor
     */
    protected double getScaleDownFactor(){
        return this.m_scaleDownFactor;
    }

    /**
     * Get the actual height of the sprite frame after scaling
     * @return height of frame in pixels
     */
    public double getScaledFrameHeight(){
        return this.m_frameHeight/this.m_scaleDownFactor;
    }

    /**
     * Get the actual width of the sprite frame after scaling
     * @return width of frame in pixels
     */
    public double getScaledFrameWidth(){
        return this.m_frameWidth/this.m_scaleDownFactor;
    }

    /**
     * This class represents a point in the (x,y) cartesian system.
     * Designed to reduce memory allocations.
     */
    public class Point{
        double  m_x = 0;
        double  m_y = 0;
        boolean m_isInitialized = false;

        double getX(){
            return this.m_x;
        }
        double getY(){
            return this.m_y;
        }

        void set(double x, double y){
            this.m_x = x;
            this.m_y = y;
            m_isInitialized = true;
        }

        boolean isInitialized(){
            return this.m_isInitialized;
        }

        void invalidate(){
            this.m_isInitialized = false;
        }
    }

    public void setAngle(float angle){
        this.m_angle = angle;
    }

    public void setPic(Bitmap bitmap){
        this.m_bitmap = bitmap;
    }

}