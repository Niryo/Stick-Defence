package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Created by yahav on 21/08/15.
 */
public class WoodenTower extends Tower {
    public static final double MAX_HP = 100.0;

    public WoodenTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.wooden_tower_blue,
              R.drawable.wooden_tower_red, MAX_HP, TowerTypes.WOODEN_TOWER);
    }

}
