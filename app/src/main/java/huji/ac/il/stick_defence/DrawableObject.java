package huji.ac.il.stick_defence;

import android.graphics.Canvas;

/**
 * Created by Nir on 25/04/2015.
 */
public interface DrawableObject {
    public void render(Canvas canvas);
    public void update(long gameTime);

}
