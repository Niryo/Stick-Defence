package huji.ac.il.stick_defence;

/**
 * Created by yahav on 31/08/15.
 */
public abstract class PotionOfLife{
    public static int LIFE_TO_ADD = 100;

    public static void playSound(){
        Sounds.getInstance().playSound(Sounds.POTION_SOUND, false);
    }
    public static String info(){
        return "Adds " + LIFE_TO_ADD + "HP to your tower.\nMax 3.\n\n" +
                "Price: " + Market.POTION_OF_LIFE_PRICE;
    }
}
