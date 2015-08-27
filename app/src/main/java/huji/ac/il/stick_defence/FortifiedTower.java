package huji.ac.il.stick_defence;

import android.content.Context;

/**
 * Created by yahav on 21/08/15.
 */
public class FortifiedTower extends Tower {
    public static final double MAX_HP = 400.0;

    public FortifiedTower(Context context, Sprite.Player player){
        super(context, player, R.drawable.fortified_tower,
              R.drawable.fortified_tower, MAX_HP, TowerTypes.FORTIFIED_TOWER);
    }

    @Override
    public int getLeftX() {
        return (int) (super.getLeftX() * 1.15);
    }

    @Override
    public int getRightX() {
        return (int) (super.getRightX() * 0.85);
    }

    public static String info(){
        return "HP: " + MAX_HP + "\n" +
                "Price: " + Market.FORTIFIED_TOWER_PRICE + "\n\n" +
                "Replaces the 'Wooden tower', the 'Big Wooden Tower' " +
                "and the 'Stone Tower'.";
    }
}
