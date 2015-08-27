package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Created by yahav on 21/08/15.
 */
public class BigWoodenTower extends Tower{
    public static final double MAX_HP = 200.0;

    public BigWoodenTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.big_wooden_tower_blue,
              R.drawable.big_wooden_tower_red, MAX_HP,
              TowerTypes.BIG_WOODEN_TOWER);
    }

    public static String info(){
        return "HP: " + MAX_HP + "\n" +
                "Price: " + Market.BIG_WOODEN_TOWER_PRICE;
    }
}
