package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class GameActivity extends Activity implements DoProtocolAction {

    private GameState gameState;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        FrameLayout game = new FrameLayout(this);
        RelativeLayout gameComponents = new RelativeLayout(this);
        this.gameState = GameState.CreateGameState(getApplicationContext());
        Client.getClientInstance().setCurrentActivity(this);
        GameSurface gameSurface = new GameSurface(this);

        //========================Send soldier Button===========================
        Button sendSoldier = new Button(this);
        sendSoldier.setText("Send");
        sendSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState.addSoldier(Sprite.Player.LEFT,0);
            }
        });
        gameComponents.addView(sendSoldier);
        //======================================================================

        //=====================ProgressBar(Tower's HP)==========================
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        ProgressBar leftProgressBar =
                new ProgressBar(this, null,
                                android.R.attr.progressBarStyleHorizontal);
        ProgressBar rightProgressBar =
                new ProgressBar(this, null,
                                android.R.attr.progressBarStyleHorizontal);

        leftProgressBar.setY(height / 5);
        leftProgressBar.setX(width / 20);

        rightProgressBar.setY(height / 5);
        rightProgressBar.setX((float) (width / 1.15));

        gameState.initProgressBar(leftProgressBar, Sprite.Player.LEFT);
        gameState.initProgressBar(rightProgressBar, Sprite.Player.RIGHT);

        gameComponents.addView(leftProgressBar);
        gameComponents.addView(rightProgressBar);
        //======================================================================

        game.addView(gameSurface);
        game.addView(gameComponents);
//        setContentView(new GameSurface(this));
        setContentView(game);

        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Waiting for opponent..");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        waitDialog.show();

        Client.getClientInstance().send(Protocol.stringify(Protocol.Action.READY_TO_PLAY));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doAction(String action, String data) {
        if (action.equals(Protocol.Action.ARROW.toString())) {
            this.gameState.addEnemyShot(Integer.parseInt(data));
        }
        if (action.equals(Protocol.Action.SOLDIER.toString())) {
            this.gameState.addSoldier(Sprite.Player.RIGHT,Long.parseLong(data));
        }

        if (action.equals(Protocol.Action.START_GAME.toString())) {

            this.gameState.setTime(System.currentTimeMillis(),Long.parseLong(data));
            this.waitDialog.dismiss();

        }

    }
}
