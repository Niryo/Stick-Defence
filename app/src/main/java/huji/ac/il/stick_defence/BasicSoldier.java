package huji.ac.il.stick_defence;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BasicSoldier {


    private Bitmap bitmap;        // the animation sequence
    private Rect frameRect;    // the rectangle to be drawn from the animation bitmap
    private int frameNumber;        // number of frames in animation
    private int currentFrame;    // the current frame
    private long frameTicker;    // the time of the last frame update
    private int framePeriod;    // milliseconds between each frame (1000/fps)

    private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;    // the height of the sprite

    private int x;                // the X coordinate of the object (top left of the image)
    private int y;                // the Y coordinate of the object (top left of the image)

    public BasicSoldier(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        currentFrame = 0;
        frameNumber = frameCount;
        spriteWidth = bitmap.getWidth() / frameCount;
        spriteHeight = bitmap.getHeight();
        frameRect = new Rect(0, 0, spriteWidth, spriteHeight);
        framePeriod = 1000 / fps;
        frameTicker = 0l;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Rect getFrameRect() {
        return frameRect;
    }

    public void setFrameRect(Rect frameRect) {
        this.frameRect = frameRect;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getFramePeriod() {
        return framePeriod;
    }

    public void setFramePeriod(int framePeriod) {
        this.framePeriod = framePeriod;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public void setSpriteWidth(int spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public void setSpriteHeight(int spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public void update(long gameTime) {
        if (gameTime > frameTicker + framePeriod) {
            frameTicker = gameTime;
            // increment the frame
            currentFrame++;
            if (currentFrame >= frameNumber) {
                currentFrame = 0;
            }
        }
        // define the rectangle to cut out sprite
        this.frameRect.left = currentFrame * spriteWidth;
        this.frameRect.right = this.frameRect.left + spriteWidth;
    }

    public void draw(Canvas canvas) {
        // where to draw the sprite
        Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
        canvas.drawBitmap(bitmap, frameRect, destRect, null);

    }


}