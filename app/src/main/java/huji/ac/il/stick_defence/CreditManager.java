package huji.ac.il.stick_defence;

import android.widget.TextView;
import android.os.Handler;

import java.text.DecimalFormat;

/**
 * This class represents the current player's points
 */
public class CreditManager extends Thread {
    private static final DecimalFormat DECIMAL_FORMAT =
            new DecimalFormat("####0.00");

    private int sleep_in_msec = 60;
    GameState gameState = GameState.getInstance();
    boolean running;
    private TextView creditTv, smallPointsTv;
    private double credits;
    private int tmpCredits;
    private double pointsToAddOrDec;
    Handler handler;

    public CreditManager(int credits) {
        this.credits = credits;
        this.tmpCredits = credits;
        handler = new Handler();
    }

    public void initCreditTv(TextView creditTv, TextView smallTv){
        this.creditTv = creditTv;
        this.smallPointsTv = smallTv;
        creditTv.setText((int)credits + "$");
    }

    synchronized public void addCredits(double creditsToAdd) {
        credits += creditsToAdd;
        pointsToAddOrDec = creditsToAdd;
    }

    synchronized public boolean decCredits(double creditsToDec,
                                           Sprite.Player player) {
        if (Sprite.Player.RIGHT == player){
            return true;
        }
        if (credits < creditsToDec) {
            return false;
        }
        credits -= creditsToDec;
        pointsToAddOrDec = -creditsToDec;
        return true;
    }

    public int getCredits() {
        return (int) this.credits;
    }

    public void startFastCreditMode(){
        this.sleep_in_msec /= 2;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        boolean doNothing;
        while (running) {
            doNothing = true;
            if (tmpCredits < (int) credits) {
                tmpCredits++;
                doNothing = false;
            } else if (tmpCredits > (int) credits) {
                tmpCredits--;
                doNothing = false;
            }

            if (!doNothing){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String text = String.valueOf(tmpCredits) + "$";
                        creditTv.setText(text);
                        text = "";
                        if (pointsToAddOrDec > 0) {
                            text =
                                  "+" + DECIMAL_FORMAT.format(pointsToAddOrDec);
                        } else if (pointsToAddOrDec < 0) {
                            text = DECIMAL_FORMAT.format(pointsToAddOrDec);
                        }
                        smallPointsTv.setText(text + "$");
                    }
                });
            }

            try {
                Thread.sleep(sleep_in_msec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
