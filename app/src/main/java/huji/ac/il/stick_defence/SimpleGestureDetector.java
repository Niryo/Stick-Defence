package huji.ac.il.stick_defence;

import android.view.MotionEvent;

/**
 * This class handles onTouch events
 */
public class SimpleGestureDetector {

    public enum Gesture {
        DOWN,
        UP,
        LEFT,
        RIGHT,
        TOUCH_DOWN,
        TOUCH_UP
    }

    private final int DISTANCE = 15;
    private float lastY;
    private float lastX;
    private GameState gameState = GameState.getInstance();

    /**
     * Receives MotionEvent and calls gameState.touch(...) respectively.
     * @param event
     */
    public void detect(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.lastX = event.getX();
                this.lastY = event.getY();
                gameState.touch(Gesture.TOUCH_DOWN,
                        new Sprite.Point(this.lastX, this.lastY));
                break;

            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float currentX = event.getX();

                if (currentY - this.lastY > DISTANCE) {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    gameState.touch(Gesture.DOWN, null); //moveDown

                }
                if (currentY - this.lastY < -DISTANCE) {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    gameState.touch(Gesture.UP, null);

                }
                if (currentX - this.lastX > DISTANCE) {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    gameState.touch(Gesture.RIGHT, null); //moveRight

                }
                if (currentX - this.lastX < -DISTANCE) {
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    gameState.touch(Gesture.LEFT, null);

                }
                break;

            case MotionEvent.ACTION_UP:
                gameState.touch(Gesture.TOUCH_UP, null);
                break;
        }
    }

}