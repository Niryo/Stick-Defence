package huji.ac.il.stick_defence;


import android.util.Log;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("custom", "application calss loaded!!!!!!!!!!!!");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Schoolbell.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Schoolbell.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Schoolbell.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");
    }
}