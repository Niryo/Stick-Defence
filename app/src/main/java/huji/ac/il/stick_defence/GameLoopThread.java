package huji.ac.il.stick_defence;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * The main game loop
 */
public class GameLoopThread extends Thread {
    private final static int MAX_FPS = 50;           // desired fps
    private final static int MAX_FRAME_SKIPS = 5;   // maximum number of frames to be skipped
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;          // the frame period


    private SurfaceHolder surfaceHolder;
    private GameSurface gameSurface;
    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public GameLoopThread(SurfaceHolder surfaceHolder, GameSurface gameSurface) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameSurface = gameSurface;
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
                    this.gameSurface.update();
                    this.gameSurface.render(canvas);


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

                    while (sleepTime < 0 && skippedFrames < MAX_FRAME_SKIPS) {
                        // we need to catch up
                        this.gameSurface.update(); // update without rendering
                        sleepTime += FRAME_PERIOD;    // add frame period to check if in next frame
                        skippedFrames++;
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
