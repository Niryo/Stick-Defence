package huji.ac.il.stick_defence;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nir on 24/08/2015.
 */
public class Sounds {
    public static final int WIN_THEME=R.raw.winning_theme;
   public static final int MAIN_THEME=R.raw.main_theme;
    public  static final int RUN_SOUND= R.raw.running_sound;
    public  static final int WALKING_SOUND = R.raw.walking_sound;
    public static final int ZOMBIE_SOUND= R.raw.zombie_sound;
    public static final int TANK_SOUND = R.raw.tank_sound;
    public static final int OLD_MAN_SOUND= R.raw.old_man_sound;
    public static final int FOG_SOUND =R.raw.wind_sound;
    public static final int MATH_BOMB =R.raw.shame;
    public static final int BOW_STRECH = R.raw.bow_strech;
    public static final int BOW_RELEASE = R.raw.bow_release;
    public static final int START_TRUMPET = R.raw.start_trumpet;
    public  static final int END_TRUMPET= R.raw.end_trumpet;
    public static final int SMALL_EXPLOSION = R.raw.small_explosion;

    private static MediaPlayer mainThemePlayer;
    private static MediaPlayer winThemePlayer;
    private static Sounds sounds=null;
    private static SoundPool soundPool;
    private ArrayList<MediaPlayer> registerdMp= new ArrayList<>();

    private Context context;
    private static HashMap<Integer,Integer> soundPoolMap;


    private Sounds(Context context){
        this.context=context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes).setMaxStreams(3)
                    .build();
        }else{
            soundPool = new SoundPool(3,AudioManager.STREAM_MUSIC,0);
        }
        soundPoolMap= new HashMap<>();
        soundPoolMap.put( RUN_SOUND, soundPool.load(context,R.raw.running_sound, 1) );
        soundPoolMap.put( WALKING_SOUND, soundPool.load(context,R.raw.walking_sound, 1) );
        soundPoolMap.put( TANK_SOUND, soundPool.load(context,R.raw.tank_sound, 1) );
        soundPoolMap.put( ZOMBIE_SOUND, soundPool.load(context,R.raw.zombie_sound, 1) );
        soundPoolMap.put( OLD_MAN_SOUND, soundPool.load(context,R.raw.old_man_sound, 1) );
        soundPoolMap.put( BOW_STRECH, soundPool.load(context,R.raw.bow_strech, 1) );
        soundPoolMap.put(BOW_RELEASE, soundPool.load(context, R.raw.bow_release, 1));
        soundPoolMap.put(SMALL_EXPLOSION, soundPool.load(context, R.raw.small_explosion, 1));

    }

    public void registerMp(MediaPlayer mp){
        this.registerdMp.add(mp);
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


    public static int playSound(int soundId, boolean loop){
        float volume =  1;
        int repeat=-1;
        if(!loop){
            repeat=0;
        }
        return  soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, repeat, 1f);
    }

    public static int playSound(int soundId){
        float volume =  1;
        return  soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, -1, 1f);
    }

    public static void stopSound(int streamId){
        soundPool.pause(streamId);
    }

    public void playTheme(int soundID){

        MediaPlayer mp = MediaPlayer.create(context, soundID);
        mp.setLooping(true);
        if(soundID== MAIN_THEME && this.mainThemePlayer==null){

            if(this.winThemePlayer!=null && this.winThemePlayer.isPlaying()){
                this.winThemePlayer.stop();
            }
        mp.start();
            mainThemePlayer=mp;
        }
        if(soundID== WIN_THEME){
            if(this.mainThemePlayer!=null && this.mainThemePlayer.isPlaying()){
                this.mainThemePlayer.stop();
            }
            mp.start();
            winThemePlayer=mp;
        }

    }

    public  MediaPlayer streamSound(int soundId){
        MediaPlayer mp = MediaPlayer.create(context, soundId);
        mp.setLooping(true);
        mp.start();
        return mp;
    }

    public  MediaPlayer streamSound(int soundId, boolean setLooping){
        MediaPlayer mp = MediaPlayer.create(context, soundId);
        mp.setLooping(setLooping);
        mp.start();
        return mp;
    }

    public void stopAllSound(){
        soundPool.autoPause();
        for(MediaPlayer mp : this.registerdMp){
            mp.stop();
        }
        this.registerdMp.clear();
        //soundPool.release();
    }

    public void stopTheme(){

            if(winThemePlayer!=null && winThemePlayer.isPlaying()){
            winThemePlayer.stop();
            winThemePlayer=null;}

            if(mainThemePlayer!=null && mainThemePlayer.isPlaying()){
            mainThemePlayer.stop();
            mainThemePlayer=null;}

    }
}
