package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private GameLoopThread m_gameLoopThread;
    private GameState gameState = GameState.CreateGameState(getContext());

    public GameSurface(Context context) {

        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // Create the GameLoopThread
        m_gameLoopThread = new GameLoopThread(getHolder(), this);

        // Make the GameSurface focusable so it can handle events
        setFocusable(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                gameState.touchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                gameState.touchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                gameState.touchUp();
                break;
        }

        return true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

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

    public void render(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Sprite soldier : gameState.getSoldiers()) {
            soldier.render(canvas);
        }

        for (Sprite tower : gameState.getTowers()) {
            tower.render(canvas);
        }

        for (Sprite bow : gameState.getBows()){
            bow.render(canvas);
        }
    }


}




