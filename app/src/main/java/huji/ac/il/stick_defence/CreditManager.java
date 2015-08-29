package huji.ac.il.stick_defence;

import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

/**
 * Created by yahav on 24/07/15.
 */
public class CreditManager extends Thread {
    //TODO: REMOVE OPPONENT'S CREDIT.
    private int sleep_in_msec = 60;
    GameState gameState = GameState.getInstance();
    boolean running;
    private TextView creditTv;
    private double credits;
    private int tmpCredits;
    Handler handler;

    public CreditManager(int credits) {

        this.credits = credits;
        this.tmpCredits = credits;
        handler = new Handler();
    }

    public void initCreditTv(TextView creditTv){
        this.creditTv = creditTv;
        creditTv.setText(credits + "$");
    }

    synchronized public void addCredits(double creditsToAdd) {
        credits += creditsToAdd;
    }

    synchronized public boolean decCredits(double creditsToDec, Sprite.Player player) {
        if (Sprite.Player.RIGHT == player){
            return true;
        }
        if (credits < creditsToDec) {
            return false;
        }
        credits -= creditsToDec;

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
      //  super.run();
        while (running) {
            if (tmpCredits < (int) credits) {
                tmpCredits++;
            } else if (tmpCredits > (int) credits) {
                tmpCredits--;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    creditTv.setText(String.valueOf(tmpCredits) + "$");
                }
            });

            try {
                Thread.sleep(sleep_in_msec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
