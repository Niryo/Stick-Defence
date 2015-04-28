package huji.ac.il.stick_defence;

import android.graphics.Canvas;

/**
 * This interface represents any object that can be draw on the screen
 */
public interface DrawableObject {
    public void render(Canvas canvas);
    public void update(long gameTime);

}
