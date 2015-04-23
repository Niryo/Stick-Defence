package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameSurface extends SurfaceView implements
        SurfaceHolder.Callback {


    private GameLoopThread thread;
    private BasicSoldier soldier;

    public GameSurface(Context context) {
        super(context);
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // create the game loop thread
        thread = new GameLoopThread(getHolder(), this);

        // make the GameSurface focusable so it can handle events
        setFocusable(true);
        //===========temp code==========
        soldier = new BasicSoldier(
                BitmapFactory.decodeResource(getResources(), R.drawable.simple_running_stick)
                , 10, 50    // initial position
                , 2250, 163    // width and height of sprite
                , 30, 9);    // FPS and number of frames in the animation

        //==============================
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }


    public void render(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        soldier.draw(canvas);
    }


    public void update() {
        soldier.update(System.currentTimeMillis());
    }

}




