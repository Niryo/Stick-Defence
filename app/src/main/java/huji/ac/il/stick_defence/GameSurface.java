package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.util.ArrayList;


/**
 * The game surface. During the game, everything takes place on this surface.
 */
public class GameSurface extends SurfaceView implements
        SurfaceHolder.Callback {

    private GameLoopThread m_gameLoopThread;

    public GameSurface(Context context, Button sendSoldier) {
        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // Create the GameLoopThread
        m_gameLoopThread = new GameLoopThread(getHolder(), this, sendSoldier);

        // Make the GameSurface focusable so it can handle events
        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_gameLoopThread.setRunning(true);
        m_gameLoopThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                m_gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the m_gameLoopThread
            }
        }
    }

    public void render(Canvas canvas, ArrayList<Sprite> sprites) {
        canvas.drawColor(Color.WHITE);

        for (Sprite spirit : sprites){
            spirit.render(canvas);
        }
    }



}




