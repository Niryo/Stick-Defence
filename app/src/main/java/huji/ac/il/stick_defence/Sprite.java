package huji.ac.il.stick_defence;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * This class represents a sprite animation object
 */
public class Sprite {
    private Bitmap bitmap;
    private int frameHeight;
    private int frameWidth;
    private Rect frameRect;  // the rectangle to be drawn from the animation bitmap
    private int currentFrame = 0;    // the current frame
    private long frameTicker = 01;    // the time of the last frame update
    private int frameNumber = 9;        // number of frames in animation
    private int fps = 4;             //the speed of the animation
    private int framePeriod = 100 / fps;    // milliseconds between each frame (1000/fps)
    private int scaleDownFactor;

    /**
     * Constructor
     *
     * @param bitmap the bitmap of the sprite
     * @param frameNumber the number of frames in the sprite
     */
    public Sprite(Bitmap bitmap, int frameNumber){
      this.bitmap=bitmap;
      this.frameNumber=frameNumber;
      this.frameHeight = bitmap.getHeight();
      this.frameWidth = (bitmap.getWidth() / frameNumber);
      this.frameRect = new Rect(0, 0, frameWidth, frameHeight);
    }

    /**
     * Sets the animation speed (fps)
     * @param fps
     */
    public void setAnimationSpeed(int fps){
        this.fps=fps;
    }

    /**
     * Update the sprite. if enough time as been passed since the last update, the
     * sprite will load the next frame.
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        if (gameTime > frameTicker + framePeriod) {
            frameTicker = gameTime;
            // increment the frame
            currentFrame++;
            if (currentFrame >= frameNumber) {
                currentFrame = 0;
            }
        }

        this.frameRect.left = currentFrame * frameWidth;
        this.frameRect.right = this.frameRect.left + frameWidth;
    }

    /**
     * Draws the sprite on the canvas in the given point. the sprite will be drawn from the
     * top left.
     *
     * @param canvas the canvas
     * @param x the x axis
     * @param y the y axis
     */
    public void render(Canvas canvas,int x,int y){
        // where to draw the sprite
        Rect destRect = new Rect(x, y, (x + getScaledFrameWidth()), (y + getScaledFrameHeight()));

        canvas.drawBitmap(this.bitmap, this.frameRect, destRect, null);
    }

    /**
     * Set the scale factor of the bitmap. if the bitmap is big and you want it to appear small, set
     * a high scale factor.
     * @param scale the amount of scale
     */
   public void setScaleDownFactor(int scale){
       this.scaleDownFactor = scale;
   }

    /**
     * Get the actual height of the sprite frame after scaling
     * @return height of frame in pixels
     */
    public int getScaledFrameHeight(){
        return this.frameHeight/this.scaleDownFactor;
    }

    /**
     * Get the actual width of the sprite frame after scaling
     * @return width of frame in pixels
     */
    public int getScaledFrameWidth(){
        return this.frameWidth/this.scaleDownFactor;
    }

}
