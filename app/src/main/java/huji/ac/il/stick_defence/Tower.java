package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by yahav on 28/04/15.
 */
public class Tower implements DrawableObject {
    public enum Position{
        LEFT,
        RIGHT
    }
    private Bitmap bitmap;
    private int frameHeight;
    private int frameWidth;
    private int frameScaledWidth;   //the frame width after scale
    private int frameScaledHeight;  //the frame height after scale
    private int screenWidth;
    private int screenHeight;
    private Rect frameRect;  // the rectangle to be drawn from the animation bitmap

    private int currentFrame=0;    // the current frame
    private long frameTicker=01;    // the time of the last frame update
    private int frameNumber=9;        // number of frames in animation
    private int fps = 4;             //the speed of the animation
    private int x =0;                // the X coordinate of the object (top left of the image)
    private int y = 100;                // the Y coordinate of the object (top left of the image)
    private int framePeriod= 100/fps;    // milliseconds between each frame (1000/fps)
    private int scaleFactor=3;
    Position m_pos;

    public Tower(Context context, Position pos) {
        int towerId = (pos == Position.RIGHT) ? R.drawable.right_tower : R.drawable.left_tower;
        this.m_pos = pos;
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), towerId);
        this.frameHeight =bitmap.getHeight();
        this.frameWidth = bitmap.getWidth();

        this.frameScaledHeight= frameHeight/scaleFactor;
        this.frameScaledWidth= frameWidth/scaleFactor;
        this.screenWidth= context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight= context.getResources().getDisplayMetrics().heightPixels;
     /*   if (pos == Position.RIGHT){
            this.frameRect = new Rect(screenWidth - frameWidth, 0, frameWidth, frameHeight);
            Log.w("yahav", "rect: " + (this.frameRect.toString()));
        } else {*/
            this.frameRect = new Rect(0, 0, frameWidth, frameHeight);
        //}

        this.y =screenHeight - frameScaledHeight; //set the y on the bottom of the screen

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
      //  this.frameRect.left = currentFrame * frameWidth;
      //  this.frameRect.right = this.frameRect.left + frameWidth;
    }

    public void render(Canvas canvas) {
        // where to draw the sprite
        Rect destRect; //= new Rect(x, y, (x + frameScaledWidth), (y + frameScaledHeight));
        if (m_pos == Position.RIGHT){
            destRect = new Rect(screenWidth - frameScaledWidth, screenHeight - frameScaledHeight, screenWidth, screenHeight);
        } else {
            destRect = new Rect(0, screenHeight - frameScaledHeight, frameScaledWidth, screenHeight);
        }

        canvas.drawBitmap(bitmap, frameRect, destRect, null);
    }
}
