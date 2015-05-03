package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by Nir on 03/05/2015.
 */
public class Arrow {
    private static Bitmap scaledArrowPic;
    private static Sprite sprite;
    private float x;
    private float y;
    private float angle;
    private float bm_offsetX;
    private float bm_offsetY;
    private Matrix matrix= new Matrix();


    public Arrow(Context context,float x, float y, float[] tan){
        this.x=x;
        this.y=y;
        this.bm_offsetX =scaledArrowPic.getWidth()/2;
        this.bm_offsetY= scaledArrowPic.getHeight()/2;
        this.angle=(float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
        updateMatrix();


    }

    private void updateMatrix(){
        matrix.postRotate(this.angle, bm_offsetX, bm_offsetY);
        matrix.postTranslate(this.x- bm_offsetX, this.y- bm_offsetY);
    }


    public void render(Canvas canvas){
        canvas.drawBitmap(scaledArrowPic, matrix, null);

    }

    public static void init(Context context,double scaleDownFactor){

           Bitmap arrowPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.arrow); // Read resource only once

        if(sprite==null){
        sprite= new Sprite();
        sprite.initSprite(context, arrowPic, 1, Sprite.Player.LEFT, 1.0);
        sprite.setScaleDownFactor(scaleDownFactor);

        }
        if(scaledArrowPic==null){
            scaledArrowPic = Bitmap.createScaledBitmap(arrowPic,   (int) sprite.getScaledFrameWidth(), (int) sprite.getScaledFrameHeight() ,false);
        }


    }


}
