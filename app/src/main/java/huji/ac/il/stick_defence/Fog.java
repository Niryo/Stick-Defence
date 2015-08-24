package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by Nir on 24/08/2015.
 */
public class Fog implements DrawableObject{
    private Sprite sprite;
    private int screenWidth;
    private int screenHeight;
    private int updateCounter=0;
    private long timeOfCreation= System.currentTimeMillis();
    private boolean active=true;
    private static int ACTIVE_TIME= 15000;
    private static int DESTROY_TIME= 18000;

    public Fog(Context context){
        Bitmap bitmap =   BitmapFactory.decodeResource(context.getResources(),
                R.drawable.smoke);
        this.sprite= new Sprite();
        this.sprite.initSprite(context,bitmap, 9,Sprite.Player.LEFT, 0.5);
        this.sprite.setAnimationSpeed(4);
        this.sprite.setLooping(false);
        this.screenHeight=GameState.getInstance().getCanvasHeight();



    }

    public void update(long gameTime){

        sprite.update(gameTime);
        long timePassed=System.currentTimeMillis()-this.timeOfCreation;
            this.updateCounter++;
        if(timePassed>ACTIVE_TIME && active){
            sprite.reverse();
            sprite.runAnimation();
            active=false;
        }
        if(timePassed>DESTROY_TIME ){
            destory();
        }


    }
    private void destory(){
    GameState.getInstance().getMiscellaneous().remove(this);
    }
    public void render(Canvas canvas) {
        sprite.render(canvas, 0, (int) (screenHeight - sprite.getScaledFrameHeight()));

    }
}
