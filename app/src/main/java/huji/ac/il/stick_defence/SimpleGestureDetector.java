package huji.ac.il.stick_defence;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Nir on 03/05/2015.
 */
public class SimpleGestureDetector {

    public static enum Gesture {
        DOWN,
        UP,
        LEFT,
        RIGHT,
        TOUCH_DOWN,
        TOUCH_UP

    }

    private final int DISTANCE = 30;
    private float lastY;
    private float lastX;
    private GameState gameState = GameState.getInstance();

    public void detect(MotionEvent event ){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
             gameState.touch(Gesture.TOUCH_DOWN);
                this.lastX=event.getX();
                this.lastY=event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float currentY= event.getY();
                float currentX= event.getX();

                if(currentY-this.lastY > DISTANCE){
                    this.lastX=event.getX();
                    this.lastY=event.getY();
                    gameState.touch(Gesture.DOWN); //moveDown

                }
                if(currentY-this.lastY < -DISTANCE){
                    this.lastX=event.getX();
                    this.lastY=event.getY();
                    gameState.touch(Gesture.UP); //todo: make enum

                }

                if(currentX-this.lastX > DISTANCE){
                    this.lastX=event.getX();
                    this.lastY=event.getY();
                    gameState.touch(Gesture.RIGHT); //moveRight

                }
                if(currentX-this.lastX < -DISTANCE) {
                    this.lastX=event.getX();
                    this.lastY=event.getY();
                    gameState.touch(Gesture.LEFT); //todo: make enum

                }
//                gameState.touch(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_UP:
               gameState.touch(Gesture.TOUCH_UP);
                break;
        }
    }

}