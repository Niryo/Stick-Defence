package huji.ac.il.stick_defence;

import android.widget.TextView;
import android.os.Handler;

/**
 * Created by yahav on 24/07/15.
 */
public class IncreaseCredits extends Thread {
    private static final int SLEEP_IN_MSEC = 60;
    GameState gameState = GameState.getInstance();
    boolean running;
    private TextView rightCreditsTv, leftCreditsTv;
    private double rightCredits, leftCredits;
    private int rightCreditsTmp, leftCreditsTmp;
    Handler handler;

    public IncreaseCredits(TextView leftCreditsTv, TextView rightCreditsTv){
        this.leftCreditsTv = leftCreditsTv;
        this.rightCreditsTv = rightCreditsTv;

        leftCredits = rightCredits = 0.0;
        rightCreditsTmp = leftCreditsTmp = 0;
        leftCreditsTv.setText("Credits: 0");
        rightCreditsTv.setText("Credits: 0");
        handler = new Handler();
    }

    synchronized public void addCredits(double creditsToAdd, Sprite.Player player){
        if (Sprite.Player.LEFT == player){
            leftCredits += creditsToAdd;
        } else {
            rightCredits += creditsToAdd;
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {
        super.run();
        while (running){
            if (leftCreditsTmp < (int) leftCredits){
                leftCreditsTmp ++;
            }
            if (rightCreditsTmp < (int) rightCredits){
                rightCreditsTmp ++;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    leftCreditsTv.setText("Credits: " +
                            String.valueOf(leftCreditsTmp));
                    rightCreditsTv.setText("Credits: " +
                            String.valueOf(rightCreditsTmp));
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
