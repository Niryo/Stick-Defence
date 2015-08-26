package huji.ac.il.stick_defence;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

/**
 * Created by Nir on 24/08/2015.
 */
public class Sounds {
    public static final int WIN_THEME=0;
   public static final int MAIN_THEME=0;
    public  static final int RUN_SOUND= R.raw.running_sound;
    public  static final int WALKING_SOUND = R.raw.walking_sound;

    private static MediaPlayer mainThemePlayer;
    private static MediaPlayer winThemePlayer;
    private static Sounds sounds=null;
    private static SoundPool soundPool;

    private Context context;
    private static HashMap<Integer,Integer> soundPoolMap;


    private Sounds(Context context){
        this.context=context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool sounds = new SoundPool.Builder()
                    .setAudioAttributes(attributes).setMaxStreams(3)
                    .build();
        }else{
            soundPool = new SoundPool(3,AudioManager.STREAM_MUSIC,0);
        }
        soundPoolMap= new HashMap<>();
        soundPoolMap.put( RUN_SOUND, soundPool.load(context,R.raw.running_sound, 1) );
        soundPoolMap.put( WALKING_SOUND, soundPool.load(context,R.raw.walking_sound, 1) );

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


    public static int playSound(int soundId){
        float volume =  1;
        return  soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, -1, 1f);
    }
    public static void stopSound(int streamId){
        soundPool.pause(streamId);
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

    public void stopAllSound(){
        soundPool.autoPause();
        //soundPool.release();
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
