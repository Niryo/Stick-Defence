package huji.ac.il.stick_defence;

import android.widget.TextView;
import android.os.Handler;

/**
 * Created by yahav on 24/07/15.
 */
public class CreditManager extends Thread {
    private static final int SLEEP_IN_MSEC = 60;
    GameState gameState = GameState.getInstance();
    boolean running;
    private TextView leftCreditsTv;
    private double leftCredits, rightCredits;
    private int tmpCredits;
    Handler handler;

    public CreditManager(TextView leftCreditsTv,
                         int leftCredits, int rightCredits){
        this.leftCreditsTv = leftCreditsTv;
        this.leftCredits = leftCredits;
        this.tmpCredits = leftCredits;

        this.rightCredits = rightCredits;

        leftCreditsTv.setText(leftCredits + "$");
        handler = new Handler();
    }

    synchronized public void addCredits(double creditsToAdd, Sprite.Player player){
        if (Sprite.Player.LEFT == player){
            leftCredits += creditsToAdd;
        } else {
            rightCredits += creditsToAdd;
        }


    }
    public int getCredits(Sprite.Player player){
        if (Sprite.Player.LEFT == player){
            return (int) this.leftCredits;
        }
        return (int) rightCredits;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {
        super.run();
        while (running){
            if (tmpCredits < (int) leftCredits){
                tmpCredits++;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    leftCreditsTv.setText(String.valueOf(tmpCredits) + "$");
                }
            });

            try {
                Thread.sleep(SLEEP_IN_MSEC);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
