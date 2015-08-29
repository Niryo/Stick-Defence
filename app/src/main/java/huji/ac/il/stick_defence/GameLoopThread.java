package huji.ac.il.stick_defence;

import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


/**
 * The main game loop
 */
public class GameLoopThread extends Thread {
    // desired fps
    private final static int MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;

    private ArtificialIntelligence ai;
    private boolean isMultiplayer;
    private SurfaceHolder surfaceHolder;
    private GameSurface gameSurface;
    private GameState gameState;
    private boolean running;
    private boolean sleep;


    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public GameLoopThread(SurfaceHolder surfaceHolder,
                          GameSurface gameSurface) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameSurface = gameSurface;
        this.gameState = GameState.getInstance();

        this.isMultiplayer = gameState.isMultiplayer();
        if (!isMultiplayer) {
            this.ai = gameState.getAi();
        }
        sleep = false;
    }

    public void sleep() {
        Log.w("yahav", "GameLoop is going to sleep");
        this.sleep = true;
    }

    public void wakeUp() {
        Log.w("yahav", "GameLoop wake up");
        this.sleep = false;
    }

    @Override
    public void run() {
        Canvas canvas;
        long beginTime;        // the time when the cycle begun
        long timeDiff;        // the time it took for the cycle to execute
        int sleepTime;        // ms to sleep (<0 if we're behind)
        int skippedFrames;    // number of frames being skipped

        sleepTime = 0;

        while (running) {

            canvas = null;
            try {

                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    skippedFrames = 0;    // resetting the frames skipped

                    if (!sleep) {
                        if (!isMultiplayer) {
                            ai.sendSoldier();
                            ai.shoot();
                        }

                        this.gameState.update();
                        this.gameSurface.render(canvas);
                    }

                    timeDiff = System.currentTimeMillis() - beginTime;

                    // calculate sleep time
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0) {
                        // if sleepTime > 0 we're OK
                        try {
                            // send the thread to sleep for a short period
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                        }
                    }

                    while (!sleep && sleepTime < 0 && skippedFrames < MAX_FRAME_SKIPS) {
                        // we need to catch up
                        this.gameState.update(); // update without rendering
                        sleepTime += FRAME_PERIOD;    // add frame period to check if in next frame
                        skippedFrames++;
                    }

                    //Check if one of the players win
                    if (gameState.isLeftPlayerWin() ||
                            gameState.isRightPlayerWin()) {
                        Log.w("custom", "game over! bye bye!");
                        running = false;
                        gameSurface.writeEndGameMessage(canvas);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        gameState.startFastCreditMode();
                        canvas = null;
                        if (!isMultiplayer){
                            ai.levelUp();
                        }
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //End of game for single player
                        if (!isMultiplayer && gameState.isRightPlayerWin()){
                            gameState.exitToMainMenu();
                        } else {
                            gameSurface.goToMarket();
                        }


                    }

                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }    // end finally
        }
    }

}
