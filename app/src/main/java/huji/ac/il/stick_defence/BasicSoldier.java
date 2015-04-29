package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BasicSoldier implements DrawableObject {
    private int FRAME_NUMBER=9;
    private int ANIMATION_SPEED=4;
    private int SCALE_FACTOR=3; //todo: make relative to sceen size


    private int screenWidth;
    private int screenHeight;

    private Sprite sprite;
    private int x = 0;                // the X coordinate of the object (top left of the image)
    private int y = 100;                // the Y coordinate of the object (top left of the image)
    private int runSpeed = 5; //todo: make the speed in pixels/seconds units.

    public BasicSoldier(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.simple_running_stick);
        this.sprite= new Sprite(bitmap, FRAME_NUMBER);
        this.sprite.setAnimationSpeed(ANIMATION_SPEED);
        this.sprite.setScaleDownFactor(SCALE_FACTOR);

        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.y = screenHeight - sprite.getScaledFrameHeight(); //set the y on the bottom of the screen
    }


    public void update(long gameTime) {
        sprite.update(gameTime);
        x += runSpeed;
        if (x > screenWidth) {
            x = 0 - sprite.getScaledFrameWidth();
        }
    }

    public void render(Canvas canvas) {
        sprite.render(canvas,x,y);
    }


}