package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Created by yahav on 21/08/15.
 */
public class StoneTower extends Tower{
    public static final double MAX_HP = 300.0;

    public StoneTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.stone_tower_blue,
                R.drawable.stone_tower_red, MAX_HP, TowerTypes.STONE_TOWER);
    }
}