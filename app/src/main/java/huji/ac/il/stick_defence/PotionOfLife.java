package huji.ac.il.stick_defence;

import android.media.MediaPlayer;

/**
 * Created by yahav on 31/08/15.
 */
public class PotionOfLife{
    public static int LIFE_TO_ADD = 100;
    private MediaPlayer mp;

    public PotionOfLife(){
        this.mp = Sounds.getInstance().streamSound(Sounds.FOG_SOUND); //TODO
    }


    public static String info(){
        return "Adds " + LIFE_TO_ADD + "HP to your tower.\nMax 3.\n\n" +
                "Price: " + Market.POTION_OF_LIFE_PRICE;
    }
}
