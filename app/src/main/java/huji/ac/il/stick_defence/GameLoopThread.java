package huji.ac.il.stick_defence;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;


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

    private SurfaceHolder m_surfaceHolder;
    private GameSurface   m_gameSurface;
    private GameState     m_gameState;
    private boolean       m_running;
    private boolean       m_sendSoldier; // TODO - handle sendSoldier of other player

    public void setRunning(boolean running) {
        this.m_running = running;
    }

    public GameLoopThread(SurfaceHolder surfaceHolder,
                          GameSurface gameSurface,
                          Button sendSoldier) {
        super();
        this.m_surfaceHolder = surfaceHolder;
        this.m_gameSurface   = gameSurface;
        this.m_gameState     = new GameState(gameSurface.getContext());
        this.m_sendSoldier   = false;
        sendSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_sendSoldier = true;
            }
        });
    }

    @Override
    public void run() {
        Canvas canvas;
        long   beginTime;        // the time when the cycle begun
        long   timeDiff;        // the time it took for the cycle to execute
        int    sleepTime;        // ms to sleep (<0 if we're behind)
        int    skippedFrames;    // number of frames being skipped

        sleepTime = 0;

        while (m_running) {
            canvas = null;
            try {
                canvas = this.m_surfaceHolder.lockCanvas();
                synchronized (m_surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    skippedFrames = 0;    // resetting the frames skipped
                    if (m_sendSoldier){
                        //TODO - handle right player addSoldier requests
                        this.m_gameState.addSoldier(m_gameSurface.getContext(),
                                                    BasicSoldier.Player.LEFT);
                        m_sendSoldier = false;
                    }
                    this.m_gameState.update();
                    this.m_gameSurface.render(canvas, m_gameState.getSpriteList());


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
                        this.m_gameState.update(); // update without rendering
                        sleepTime += FRAME_PERIOD;    // add frame period to check if in next frame
                        skippedFrames++;
                    }

                }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    m_surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }    // end finally
        }
    }

}
