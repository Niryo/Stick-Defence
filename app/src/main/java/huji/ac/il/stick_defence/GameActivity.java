package huji.ac.il.stick_defence;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;


public class GameActivity extends Activity implements DoProtocolAction {
    private GameState   gameState;
    private ProgressDialog waitDialog;
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
        /*File file = new File(getFilesDir(), GameState.FILE_NAME);
        boolean newGame = true;
        if (file.exists()){
            newGame = false;
        }*/
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

        //======================Send soldiers Buttons===========================
        Button sendBasicSoldier = new Button(this);
        sendBasicSoldier.
                setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.basic_soldier_icon, 0, 0, 0);
        sendBasicSoldier.
                setId(PlayerStorage.SoldiersEnum.BASIC_SOLDIER.ordinal() + 1);
        sendBasicSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState.addSoldier(Sprite.Player.LEFT, 0,
                        Protocol.Action.BASIC_SOLDIER);
            }
        });
        gameComponents.addView(sendBasicSoldier);

        Button sendBazookaSoldier = new Button(this);
        sendBazookaSoldier.
                setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.bazooka_icon, 0, 0, 0);
        sendBazookaSoldier.
                setId(PlayerStorage.SoldiersEnum.BAZOOKA_SOLDIER.ordinal() + 1);
        sendBazookaSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState.addSoldier(Sprite.Player.LEFT, 0,
                        Protocol.Action.BAZOOKA_SOLDIER);
            }
        });
        RelativeLayout.LayoutParams bazookaLayoutParams =
                new RelativeLayout.
                        LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                     RelativeLayout.LayoutParams.WRAP_CONTENT);
        bazookaLayoutParams.addRule(RelativeLayout.RIGHT_OF, sendBasicSoldier.getId());
        gameComponents.addView(sendBazookaSoldier, bazookaLayoutParams);
        sendBazookaSoldier.setVisibility(View.INVISIBLE);
        gameState.initBazookaSoldierButton(sendBazookaSoldier);

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

        //============================== Points ================================

        TextView pointsTv = new TextView(this);

        pointsTv.setTextSize(50);
        pointsTv.setTextColor(Color.rgb(255,215,0));
        RelativeLayout.LayoutParams pointsLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pointsLayoutParams.addRule(RelativeLayout.RIGHT_OF, sendBazookaSoldier.getId());
        gameState.initCredits(pointsTv);

        gameComponents.addView(pointsTv, pointsLayoutParams);
     //   gameComponents.addView(rightPointsTv);




        //======================================================================
        game.addView(gameSurface);
        game.addView(gameComponents);
//        setContentView(new GameSurface(this));
        setContentView(game);



       if (isMultiplayer){
            waitDialog = new ProgressDialog(this);
            waitDialog.setMessage("Waiting for opponent..");
            waitDialog.setIndeterminate(true);
            waitDialog.setCancelable(false);
            waitDialog.show();

            Client.getClientInstance().send(Protocol.stringify(Protocol.Action.READY_TO_PLAY));



            AlertDialog.Builder pauseDialogBuilder;
            pauseDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle("Pause")
                    .setMessage("Wait! Other player in a break")
                    .setIcon(android.R.drawable.ic_dialog_alert);

            pauseDialog = pauseDialogBuilder.create();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("yahav", "onResume");
    }

    @Override
    protected void onPause() {
        if (isMultiplayer){
            Client.getClientInstance().
                    send(Protocol.stringify(Protocol.Action.PAUSE));
        }
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
            File file = new File(getFilesDir(), PlayerStorage.FILE_NAME);
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
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);
        switch (action){
            case ARROW:
                int arrowDistance =Integer.parseInt(Protocol.getData(rawInput));
                long timeStamp = Protocol.getTimeStamp(rawInput);
                this.gameState.addEnemyShot(arrowDistance, timeStamp);
                break;


            case BASIC_SOLDIER:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                                          Protocol.getTimeStamp(rawInput),
                                          Protocol.Action.BASIC_SOLDIER);
                break;
            case BAZOOKA_SOLDIER:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Protocol.getTimeStamp(rawInput),
                        Protocol.Action.BAZOOKA_SOLDIER);
                break;
            case START_GAME:
                this.gameState.setTime(System.currentTimeMillis(),
                        Long.parseLong(Protocol.getData(rawInput)));
                this.waitDialog.dismiss();
                break;

            case PAUSE:
                gameSurface.goToMarket();
                this.gameSurface.sleep();
                break;

            case RESUME:
                this.gameSurface.wakeUp();
                break;

            case GAME_OVER:
                gameState.saveAndFinish();
                gameSurface.goToMarket();
                break;
        }


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to quit to main menu?")
                .setPositiveButton(android.R.string.yes,
                                   new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(getFilesDir(),
                                             PlayerStorage.FILE_NAME);
                        if (!file.delete()){
                            Log.w("yahav", "Failed to delete file");
                        }
                        Intent intent = new Intent(getApplicationContext(),
                                                   MainMenu.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no,
                                   new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
