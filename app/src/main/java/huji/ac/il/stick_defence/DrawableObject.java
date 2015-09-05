package huji.ac.il.stick_defence;

import android.graphics.Canvas;

/**
 * This interface represents any object that can be drawn on the screen
 */
public interface DrawableObject {

    /**
     * Draw the object
     * @param canvas the canvas to draw on
     */
    void render(Canvas canvas);

    /**
     * Update object's place
     * @param gameTime
     */
    void update (long gameTime);

}
