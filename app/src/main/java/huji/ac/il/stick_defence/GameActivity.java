package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;


public class GameActivity extends Activity implements DoProtocolAction {

    private GameState   gameState;
    private AlertDialog waitDialog;
    private boolean isMultiplayer;
    private GameSurface gameSurface;

    private AlertDialog         pauseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        Log.w("yahav", "Starting GameActivity");
        boolean newGame = getIntent().getBooleanExtra("NewGame", true);
        if (newGame){
            Log.w("yahav", "New game");
            GameState.reset();
            this.gameState = GameState.CreateGameState(getApplicationContext());
            isMultiplayer = getIntent().getBooleanExtra("Multiplayer", true);
        } else {
            this.gameState = GameState.CreateGameState(getApplicationContext());
            isMultiplayer = gameState.isMultiplayer();
        }
//        setContentView(R.layout.activity_main);
        FrameLayout game = new FrameLayout(this);
        RelativeLayout gameComponents = new RelativeLayout(this);

        if (!isMultiplayer){
            this.gameState.setSinglePlayer();
        }
        gameState.resetUpdateTimes();
        Client.getClientInstance().setCurrentActivity(this);
        gameSurface = new GameSurface(this, isMultiplayer);

        //========================Send soldier Button===========================
        Button sendSoldier = new Button(this);
        sendSoldier.setText("Send");
        sendSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState.addSoldier(Sprite.Player.LEFT, 0);
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
        ProgressBar leftProgressBar = new ProgressBar(this, null, android.R
                .attr.progressBarStyleHorizontal);
        ProgressBar rightProgressBar = new ProgressBar(this, null, android.R
                .attr.progressBarStyleHorizontal);

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
        if (isMultiplayer){
            ProgressDialog dialog = new ProgressDialog(this);
            //todo: ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Thinking...");
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
//        dialog.show();
            waitDialog = new AlertDialog.Builder(this)
                    //.setTitle("Waiting for opponent..")
                    .setPositiveButton("ready", new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Client.getClientInstance().send(Protocol.stringify
                                    (Protocol.Action.READY_TO_PLAY));
                        }
                    }).setNegativeButton("Wipe all data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(getFilesDir(), GameState.fileName);
                            Log.w("yahav", getFilesDir().toString());
                            if (!file.delete()) {
                                Log.w("yahav", "Failed to delete file");
                            } else {
                                Log.w("yahav", "File deleted successfully");
                            }
                            Intent mainMenuIntent =
                                    new Intent(getApplicationContext(),
                                            MainMenu.class);
                            startActivity(mainMenuIntent);
                            finish();
                        }
                    }).setMessage("Waiting for opponent..").setIcon(android.R
                            .drawable.ic_dialog_alert).setCancelable(false).show();

            //Client.getClientInstance().send(Protocol.stringify(Protocol.Action
            // .READY_TO_PLAY));

            AlertDialog.Builder pauseDialogBuilder;
            pauseDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle("Pause")
                    .setMessage("Wait! Other player in a break")
                    .setIcon(android.R.drawable.ic_dialog_alert);

            pauseDialog = pauseDialogBuilder.create();
        }
    }

    @Override
    protected void onPause() {
        if (isMultiplayer){
            Client.getClientInstance().
                    send(Protocol.stringify(Protocol.Action.PAUSE));
        }

        gameState.save(new File(getFilesDir(), GameState.fileName));
        gameSurface.stopGameLoop();
        super.onPause();
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
        if (id == R.id.exit_to_main_menu) {
            File file = new File(getFilesDir(), GameState.fileName);
            if (!file.delete()){
                Log.w("yahav", "Failed to delete file");
            } else {
                Log.w("yahav", "File deleted successfully");
            }
            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doAction(String action, String data) {
        Protocol.Action protAction = Protocol.Action.valueOf(action);

        switch (protAction){
            case ARROW:
                this.gameState.addEnemyShot(Integer.parseInt(data));
                break;

            case SOLDIER:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Long.parseLong(data));
                break;

            case START_GAME:
                this.gameState.setTime(System.currentTimeMillis(),
                        Long.parseLong(data));
                this.waitDialog.dismiss();
                break;

            case PAUSE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pauseDialog.show();
                    }
                });

                this.gameSurface.sleep();
                break;

            case RESUME:
                pauseDialog.cancel();
                this.gameSurface.wakeUp();
                break;
        }


    }
}
