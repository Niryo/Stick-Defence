package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;


/**
 * The game surface. During the game, everything takes place on this surface.
 */
public class GameSurface extends SurfaceView implements
        SurfaceHolder.Callback {

    private GameLoopThread gameLoopThread;
    private GameState gameState = GameState.CreateGameState(getContext());
    private SimpleGestureDetector simpleGestureDetector= new SimpleGestureDetector();

    public GameSurface(Context context) {

        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // Create the GameLoopThread
        gameLoopThread = new GameLoopThread(getHolder(), this);

        // Make the GameSurface focusable so it can handle events
        setFocusable(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        this.simpleGestureDetector.detect(event);
        return true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the gameLoopThread
            }
        }
    }

    public void render(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        for (Tower tower : gameState.getTowers()) {
            tower.render(canvas);
        }

        for (BasicSoldier soldier : gameState.getSoldiers()) {
            soldier.render(canvas);
        }

        for (Bow bow : gameState.getBows()){
            bow.render(canvas);
        }
    }


}




