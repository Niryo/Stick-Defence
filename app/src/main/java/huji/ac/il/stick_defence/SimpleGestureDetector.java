package huji.ac.il.stick_defence;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Nir on 03/05/2015.
 */
public class SimpleGestureDetector {
    private final int DISTANCE = 30;
    private float lastY;
    private float lastX;
    private GameState gameState = GameState.getInstance();

public void detect(MotionEvent event ){

    switch (event.getAction()){
        case MotionEvent.ACTION_DOWN:
//                gameState.touchDown(event.getX(), event.getY());
            this.lastX=event.getX();
            this.lastY=event.getY();
            break;

        case MotionEvent.ACTION_MOVE:
            float currentY= event.getY();
            float currentX= event.getX();

            if(currentY-this.lastY > DISTANCE){
                this.lastX=event.getX();
                this.lastY=event.getY();
                gameState.touchMove(1); //moveDown
                Log.w("custom", "move down");
            }
            if(currentY-this.lastY < -DISTANCE){
                this.lastX=event.getX();
                this.lastY=event.getY();
                gameState.touchMove(2); //todo: make enum
                Log.w("custom", "move up");
            }

            if(currentX-this.lastX > DISTANCE){
                this.lastX=event.getX();
                this.lastY=event.getY();
                gameState.touchMove(3); //moveRight
                Log.w("custom", "move right");
            }
            if(currentX-this.lastX < -DISTANCE) {
                this.lastX=event.getX();
                this.lastY=event.getY();
                gameState.touchMove(4); //todo: make enum
                Log.w("custom", "move left");
            }
//                gameState.touchMove(event.getX(), event.getY());
            break;

        case MotionEvent.ACTION_UP:
//                gameState.touchUp();
            break;
    }
}

}
