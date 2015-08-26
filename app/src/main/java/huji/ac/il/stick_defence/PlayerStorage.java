package huji.ac.il.stick_defence;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by yahav on 25/07/15.
 */


public class PlayerStorage implements Serializable {
    public enum PurchasesEnum {
        BASIC_SOLDIER,
        ZOMBIE,
        SWORDMAN,
        BOMB_GRANDPA,
        TANK,
        BAZOOKA_SOLDIER,
        MATH_BOMB,
        WOODEN_TOWER,
        BIG_WOODEN_TOWER,
        STONE_TOWER,
        FORTIFIED_TOWER,
        FOG
    }

    public static final String FILE_NAME = "stick_defence.sav";
    //    private String name;
    private int credits;
    private boolean items[];
    private transient Context context;
    private transient boolean newGame;

    public PlayerStorage(Context context,/* String name,*/ int credits) {
        this.context = context;
        //    this.name = name;
        this.credits = credits;
        this.items = new boolean[PurchasesEnum.values().length];
        this.items[PurchasesEnum.BASIC_SOLDIER.ordinal()] = true;
        newGame = true;
    }

   /* public String getName(){
        return this.name;
    }*/

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public boolean isPurchased(PurchasesEnum iItem) {
        return this.items[iItem.ordinal()];
    }

    public void buy(PurchasesEnum item) {
        this.items[item.ordinal()] = true;
    }

    public boolean isGameInProcess() {
        return !this.newGame;
    }

    public void save() {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            Log.w("yahav", "Saving to" + context.getFilesDir());
            ObjectOutputStream oos =
                    new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStorage load(Context context) {
        PlayerStorage ps = new PlayerStorage(context, 0);
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (file.exists()) {
            Log.w("yahav", "Loading from" + context.getFilesDir());
            try {
                ObjectInputStream ois =
                        new ObjectInputStream(new FileInputStream(file));
                PlayerStorage readPs = (PlayerStorage) ois.readObject();
                ois.close();
                //    this.name = readPs.name;
                ps.credits = readPs.credits;
                ps.items = readPs.items;
                ps.newGame = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); //May be deleted
            }
        }
        return ps;
    }
}
