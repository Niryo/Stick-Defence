package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * This class represents a wooden tower
 */
public class WoodenTower extends Tower {
    public static final double MAX_HP = 100.0;

    /**
     * Constructor
     * @param context the context
     * @param player the player
     */
    public WoodenTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.wooden_tower_blue,
              R.drawable.wooden_tower_red, MAX_HP, TowerTypes.WOODEN_TOWER);
    }

    @Override
    public int getLeftX() {
        return (int) (super.getLeftX() * 1.07);
    }

    @Override
    public int getRightX() {
        return (int) (super.getRightX() * 0.93);
    }
}
