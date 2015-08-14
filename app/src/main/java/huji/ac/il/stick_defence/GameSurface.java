package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;


/**
 * The game surface. During the game, everything takes place on this surface.
 */
public class GameSurface extends SurfaceView implements
        SurfaceHolder.Callback {

    private GameLoopThread gameLoopThread;
    private GameState gameState = GameState.getInstance();
    private SimpleGestureDetector simpleGestureDetector =
            new SimpleGestureDetector();
    private Context context;
    private Bitmap scaledBackground;

    public GameSurface(Context context) {

        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // Create the GameLoopThread
        gameLoopThread = new GameLoopThread(getHolder(), this);

        // Make the GameSurface focusable so it can handle events
        setFocusable(true);

        this.context = context;

    }

    public void goToMarket() {
        gameState.saveAndFinish();
        stopGameLoop();
        Intent gameIntent = new Intent(context, Market.class);
// TODO - Save an object with points[player], nPlayers, isMultiplayer, etc.
        gameIntent.putExtra("Multiplayer", gameState.isMultiplayer());
        context.startActivity(gameIntent);
        ((Activity) context).finish();
    }

    public void stopGameLoop() {
        if (null != gameLoopThread) {
            gameLoopThread.setRunning(false);
     /*       try{
                gameLoopThread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }*/

        }
    }

    public void sleep() {
        gameLoopThread.sleep();
    }

    public void wakeUp() {
        gameLoopThread.wakeUp();
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
        Bitmap background = BitmapFactory.decodeResource(getResources(),
                R.drawable.background);
        float scale = (float) background.getHeight() / (float) getHeight();
        int newWidth = Math.round(background.getWidth() / scale);
        int newHeight = Math.round(background.getHeight() / scale);
        scaledBackground = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        if (gameLoopThread.getState() == Thread.State.NEW) {
            Log.w("yahav", "surfaceCreated");
            gameLoopThread.setRunning(true);
            gameLoopThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.w("yahav", "surfaceDestroyed");
        boolean retry = true;
        while (retry) {
            try {
                gameLoopThread.setRunning(false);
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                // try again shutting down the gameLoopThread
            }
        }
    }

    public void render(Canvas canvas) {
        //    canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(scaledBackground, 0, 0, null); // draw the background
        for (Tower tower : gameState.getTowers()) {
            tower.render(canvas);
        }

        for (BazookaBullet bullet : gameState.getBazookaBullets()) {
            bullet.render(canvas);
        }

        for (Soldier soldier : gameState.getSoldiers()) {
            soldier.render(canvas);
        }

        for (Bow bow : gameState.getBows()) {
            bow.render(canvas);
        }
        for (Arrow arrow : gameState.getArrows()) {
            arrow.render(canvas);
        }
    }

}




