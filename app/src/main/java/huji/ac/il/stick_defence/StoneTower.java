package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Created by yahav on 21/08/15.
 */
public class StoneTower extends Tower{
    public static final int MAX_HP = 300;

    public StoneTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.stone_tower_blue,
                R.drawable.stone_tower_red, MAX_HP, TowerTypes.STONE_TOWER);
    }

    public static String info(){
        return "HP: " + MAX_HP + "\n" +
               "Strong and steady tower.\n" +
               "Replaces the 'Wooden tower' and the 'Big Wooden Tower'.\n\n" +
               "Price: " + Market.STONE_TOWER_PRICE;
    }
}
