package huji.ac.il.stick_defence;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;

/**
 * Created by Nir on 24/08/2015.
 */
public class Sounds {
    public static final int WIN_THEME=0;
   public static final int MAIN_THEME=0;
    private static MediaPlayer mainThemePlayer;
    private static MediaPlayer winThemePlayer;
    private static Sounds sounds=null;

    private Context context;
    private HashMap soundPoolMap;

    private Sounds(Context context){
        this.context=context;

    }

    public static Sounds create(Context context){
       if(sounds==null){
           sounds= new Sounds(context);
       }
        return sounds;
    }
    public static Sounds getInstance(){
        return sounds;
    }




    public void playTheme(int soundID){
        MediaPlayer mp = MediaPlayer.create(context, MAIN_THEME);
        mp.setLooping(true);
        mp.start();
        if(soundID== WIN_THEME){
            winThemePlayer=mp;
        }else{
            mainThemePlayer=mp;
        }
    }

    public void stopTheme(int soundID){
        if(soundID== WIN_THEME){
            if(winThemePlayer!=null){
            winThemePlayer.stop();
            winThemePlayer=null;}
        }else{
            if(mainThemePlayer!=null){
            mainThemePlayer.stop();
            mainThemePlayer=null;}
        }
    }
}
