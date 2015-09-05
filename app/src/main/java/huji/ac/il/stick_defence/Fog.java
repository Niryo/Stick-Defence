package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;

/**
 * This class represents the fog weapon
 */
public class Fog implements DrawableObject{
    private Sprite sprite;
    private int screenHeight;
    private long timeOfCreation= System.currentTimeMillis();
    private boolean active=true;
    private static final int ACTIVE_TIME= 15000;
    private static final int DESTROY_TIME= 18000;
    private MediaPlayer mp;
    private static Bitmap fogPic;

    /**
     * Constructor
     * @param context the context
     */
    public Fog(Context context){
        if (null == fogPic){
            fogPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.smoke);
        }
        this.sprite= new Sprite();
        this.sprite.initSprite(fogPic, 9,Sprite.Player.LEFT, 0.5);
        this.sprite.setAnimationSpeed(4);
        this.sprite.setLooping(false);
        this.screenHeight=GameState.getInstance().getCanvasHeight();
        this.mp=Sounds.getInstance().streamSound(Sounds.FOG_SOUND);
    }

    @Override
    public void update(long gameTime){
        sprite.update(gameTime);
        long timePassed=System.currentTimeMillis()-this.timeOfCreation;
        if(timePassed>ACTIVE_TIME && active){
            sprite.reverse();
            sprite.runAnimation();
            active=false;
        }
        if(timePassed>DESTROY_TIME ){
            destroy();
        }
    }

    @Override
    public void render(Canvas canvas) {
        sprite.render(canvas, 0,
                      (int) (screenHeight - sprite.getScaledFrameHeight()));
    }

    private void destroy(){
        mp.stop();
        GameState.getInstance().getMiscellaneous().remove(this);
    }

    public static String info(){
        return "Blurs your enemy sight.\n" +
                "Multiplayer only.\n\n" +
                "Price: " + Market.FOG_PRICE;
    }
}
