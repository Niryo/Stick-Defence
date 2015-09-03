package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Represents a big wooden tower
 */
public class BigWoodenTower extends Tower{
    public static final int MAX_HP = 200;

    /**
     * Constructor
     * @param context the context
     * @param player the player
     */
    public BigWoodenTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.big_wooden_tower_blue,
              R.drawable.big_wooden_tower_red, MAX_HP,
              TowerTypes.BIG_WOODEN_TOWER);
    }

    public static String info(){
        return "HP: " + MAX_HP + "\n\n" +
                "Price: " + Market.BIG_WOODEN_TOWER_PRICE;
    }
}
