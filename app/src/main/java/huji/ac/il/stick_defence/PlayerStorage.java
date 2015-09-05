package huji.ac.il.stick_defence;

/**
 * Stores all player's data.
 */
public class PlayerStorage {
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
        FOG,
        POTION_OF_LIFE
    }

    private int credits;
    private boolean items[];
    private transient boolean newGame;

    public PlayerStorage(int credits) {
        this.credits = credits;
        this.items = new boolean[PurchasesEnum.values().length];
        this.items[PurchasesEnum.BASIC_SOLDIER.ordinal()] = true;
        newGame = true;
    }

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

    public void use(PurchasesEnum item){
        this.items[item.ordinal()] = false;
    }

    public boolean isGameInProcess() {
        return !this.newGame;
    }

}
