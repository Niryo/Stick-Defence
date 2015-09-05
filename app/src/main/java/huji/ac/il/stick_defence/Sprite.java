package huji.ac.il.stick_defence;

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
     * Represents left or right PLAYER
     */
    public enum Player { LEFT, RIGHT }

    private static final int DEFAULT_FPS = 40;
    private static final int MILLISEC_IN_SEC = 1000;

    private Bitmap bitmap;
    private Rect frameRect; // the rectangle to be drawn from the animation bitmap
    private RectF destRect;
    private int frameHeight;
    private int frameWidth;
    private int currentFrame = 0; // the current frame
    private int nFrames; // number of frames in animation
    private int fps = DEFAULT_FPS; //the speed of the animation
    // milliseconds between each frame (1000/fps)
    private int framePeriod = MILLISEC_IN_SEC / fps;
    private long frameTicker = 0l;    // the time of the last frame update
    private double scaleDownFactor;
    private Player player;
    private boolean isLooping=true;
    private boolean runLoopFlag =true;


    /**
     * Default Constructor
     */
    public Sprite() {}

    /**
     * Initialize the sprite. Must be called after construct.
     *
     * @param bitmap              the bitmap of the sprite
     * @param nFrames             the number of frames in the sprite
     * @param player              left or right PLAYER
     * @param screenHeightPortion sprite height in relation to the screen height.
     *                            0-1 double. For instance, 0.5 will cause the
     *                            sprite to span over a half of the screen height.
     */
    public void initSprite(Bitmap bitmap, int nFrames, Player player,
                           double screenHeightPortion) {

        this.bitmap = bitmap;
        this.player = player;
        this.nFrames = nFrames;
        this.frameHeight = bitmap.getHeight();
        this.frameWidth = (bitmap.getWidth() / nFrames);
        this.frameRect = new Rect(0, 0, frameWidth, frameHeight);
        this.destRect = new RectF();
        int screenHeight = GameState.getCanvasHeight();
        setScaleDownFactor(((double) this.frameHeight / (double)
                screenHeight) / screenHeightPortion);
    }

    /**
     * Mirror bitmap.
     *
     * @param src the bitmap to mirror
     * @return a mirrored bitmap
     */
    public static Bitmap mirrorBitmap(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src
                .getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    /**
     * Sets the animation speed (fps)
     *
     * @param fps the fps
     */
    public void setAnimationSpeed(int fps) {
        this.fps = fps;
        this.framePeriod = 1000 / fps;
    }

    /**
     * Update the sprite. if enough time as been passed since the last update, the
     * sprite will load the next frame.
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        if (gameTime > frameTicker + framePeriod && this.runLoopFlag) {
            frameTicker = gameTime;
            if (nFrames > 1) {
                // increment the frame
                if (player == Player.LEFT) {
                    currentFrame++;
                    if (currentFrame >= nFrames) {
                        if(isLooping) {
                            currentFrame = 0;
                        }
                        else{
                            currentFrame=nFrames - 1;
                            runLoopFlag =false;
                        }
                    }
                } else {
                    currentFrame--;
                    if (currentFrame < 0) {
                        if(isLooping){
                        currentFrame = nFrames - 1;
                        }else{
                            currentFrame=0;
                        }
                    }
                }

            }
            this.frameRect.left = this.currentFrame * frameWidth;
            this.frameRect.right = this.frameRect.left + frameWidth;
        }
    }

    int getCurrentFrame() {
        return this.currentFrame;
    }

    /**
     * Draws the sprite on the canvas in the given point.
     * the sprite will be drawn from the
     * top left.
     *
     * @param canvas the canvas
     * @param x      the x axis
     * @param y      the y axis
     */
    public void render(Canvas canvas, int x, int y) {
        // where to draw the sprite
        destRect.set(x,
                y,
                (x + (int) getScaledFrameWidth()),
                (y + (int) getScaledFrameHeight()));

        canvas.drawBitmap(this.bitmap, this.frameRect, destRect, null);
    }

    /**
     * Set the scale factor of the bitmap. if the bitmap is big and you want it to appear small, set
     * a high scale factor.
     *
     * @param scale the amount of scale
     */
    public void setScaleDownFactor(double scale) {
        this.scaleDownFactor = scale;
    }

    /**
     * Returns the scan down factor, that is a factor that shrink or enlarge
     * the sprite in relation to the screen dimensions.
     *
     * @return scaleDownFactor
     */
    protected double getScaleDownFactor() {
        return this.scaleDownFactor;
    }

    /**
     * Get the actual height of the sprite frame after scaling
     *
     * @return height of frame in pixels
     */
    public double getScaledFrameHeight() {
        return this.frameHeight / this.scaleDownFactor;
    }

    /**
     * Get the actual width of the sprite frame after scaling
     *
     * @return width of frame in pixels
     */
    public double getScaledFrameWidth() {
        return this.frameWidth / this.scaleDownFactor;
    }

    /**
     * This class represents a point in the (x,y) cartesian system.
     * Designed to reduce memory allocations.
     */
    public static class Point {
        float x = 0;
        float y = 0;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float getX() {
            return this.x;
        }

        float getY() { return this.y; }

        void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public void setPic(Bitmap bitmap, int nFrames) {
        this.bitmap = bitmap;
        this.nFrames = nFrames;
        this.frameHeight = bitmap.getHeight();
        this.frameWidth = (bitmap.getWidth() / nFrames);
    }

    public void setLooping(boolean looping){
        this.isLooping= looping;
    }

    public void reverse(){
        if (this.player==Player.RIGHT){
            this.player=Player.LEFT;
        }else{
            this.player=Player.RIGHT;
        }
    }
    public void runAnimation(){ this.runLoopFlag =true; }
}