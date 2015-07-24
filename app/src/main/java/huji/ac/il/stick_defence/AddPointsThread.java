package huji.ac.il.stick_defence;

import android.widget.TextView;
import android.os.Handler;

/**
 * Created by yahav on 24/07/15.
 */
public class AddPointsThread extends Thread {
    public static final int SLEEP_IN_MSEC = 60;
    GameState gameState = GameState.getInstance();
    boolean running;
    private TextView rightPointsTv, leftPointsTv;
    private int rightPoints, leftPoints;
    private int rightPointsTmp, leftPointsTmp;
    Handler handler;

    public AddPointsThread(TextView leftPointsTv, TextView rightPointsTv){
        this.leftPointsTv = leftPointsTv;
        this.rightPointsTv = rightPointsTv;

        leftPoints = leftPointsTmp = rightPointsTmp = rightPoints = 0;
        leftPointsTv.setText("0");
        rightPointsTv.setText("0");
        handler = new Handler();
    }

    synchronized public void addPoints (int points, Sprite.Player player){
        if (Sprite.Player.LEFT == player){
            leftPoints += points;
        } else {
            rightPoints += points;
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public int getLeftPoints(){
        return leftPointsTmp;
    }
    public int getRightPoints(){
        return rightPointsTmp;
    }

    @Override
    public void run() {
        super.run();
        while (running){
            if (leftPointsTmp < leftPoints){
                leftPointsTmp ++;
            }
            if (rightPointsTmp < rightPoints){
                rightPointsTmp ++;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    leftPointsTv.setText(String.valueOf(leftPointsTmp));
                    rightPointsTv.setText(String.valueOf(rightPointsTmp));
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
