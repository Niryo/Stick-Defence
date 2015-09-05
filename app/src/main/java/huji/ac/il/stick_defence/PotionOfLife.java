package huji.ac.il.stick_defence;

/**
 * This class represents the potion of life
 */
public abstract class PotionOfLife{
    public static int LIFE_TO_ADD = 100;

    public static void playSound(){
        Sounds.playSound(Sounds.POTION_SOUND, false);
    }
    public static String info(){
        return "Adds " + LIFE_TO_ADD + "HP to your tower.\nMax 1.\n\n" +
                "Price: " + Market.POTION_OF_LIFE_PRICE;
    }
}
