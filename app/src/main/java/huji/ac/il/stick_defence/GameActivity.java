package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class GameActivity extends Activity implements DoProtocolAction {
    private GameState gameState;
    private ProgressDialog waitDialog;
    private boolean isMultiplayer;
    private GameSurface gameSurface;
    private AlertDialog pauseDialog;
    private LinearLayout gameComponentsLayout;
    private ArrayList<Button> buttons;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        int screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;

        int newScreenHeight = (int) Math.round(((double) 9 * screenWidth) / 16);//set the height to be proportional to the width

        GameState.reset();
        this.gameState = GameState.CreateGameState(getApplicationContext(),this, screenWidth, newScreenHeight);
        isMultiplayer = getIntent().getBooleanExtra("Multiplayer", true);

        gameComponentsLayout = (LinearLayout) findViewById(R.id.game_components);

        if (!isMultiplayer) {
            this.gameState.setSinglePlayer();
        }

        Client.getClientInstance().setCurrentActivity(this);
        gameSurface = new GameSurface(this);
        FrameLayout surfaceFrame = (FrameLayout) findViewById(R.id.canvas_frame);
        ViewGroup.LayoutParams params = surfaceFrame.getLayoutParams();
        params.height = newScreenHeight;
        surfaceFrame.setLayoutParams(params);
        surfaceFrame.addView(gameSurface);


        //======================Add Buttons===========================
        LinearLayout buttonsLayout = new LinearLayout(this);
        //buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout firstLineLayout = new LinearLayout(this);
        firstLineLayout.setOrientation(LinearLayout.HORIZONTAL);




        Button sendBasicSoldier = new Button(this);
        this.buttons= new ArrayList<>();
        sendBasicSoldier.
                setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.basic_soldier_icon, 0, 0, 0);
        sendBasicSoldier.
                setId(PlayerStorage.PurchasesEnum.BASIC_SOLDIER.ordinal() + 1);
        sendBasicSoldier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameState.addSoldier(Sprite.Player.LEFT, 0,
                        Protocol.Action.BASIC_SOLDIER);
            }
        });
        buttonsLayout.addView(sendBasicSoldier);
        buttons.add(sendBasicSoldier);

        if (gameState.isPurchased(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER)){
            Button sendBazookaSoldier = new Button(this);
            sendBazookaSoldier.
                    setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.bazooka_icon, 0, 0, 0);
            /*sendBazookaSoldier.
                    setId(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER.ordinal() + 1);*/
            sendBazookaSoldier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameState.addSoldier(Sprite.Player.LEFT, 0,
                            Protocol.Action.BAZOOKA_SOLDIER);
                }
            });
            buttonsLayout.addView(sendBazookaSoldier);
            buttons.add(sendBazookaSoldier);
            gameState.initBazookaSoldierButton(sendBazookaSoldier);
        }

        if (gameState.isPurchased(PlayerStorage.PurchasesEnum.MATH_BOMB)){
            Button sendMathBomb = new Button(this);
            sendMathBomb.setTag("MathBomb");
            sendMathBomb.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.math_bomb, 0, 0, 0);
            sendMathBomb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameState.sendMathBomb();
                    v.setEnabled(false);
                    ((Button) v).setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.math_bomb_sent, 0, 0, 0);
                }
            });

            buttonsLayout.addView(sendMathBomb);
            buttons.add(sendMathBomb);
        }


        gameComponentsLayout.addView(firstLineLayout);
        gameState.setButtonsComponent(buttons);


        firstLineLayout.addView(buttonsLayout);

        //=====================ProgressBar(Tower's HP)==========================

        ProgressBar leftProgressBar = new ProgressBar(this, null, android.R
                .attr.progressBarStyleHorizontal);

        Drawable blueStyle = ContextCompat.getDrawable(this, R.drawable.blue_progressbar);
        leftProgressBar.setProgressDrawable(blueStyle);
        leftProgressBar.setAlpha(0.7f);
        ProgressBar rightProgressBar = new ProgressBar(this, null, android.R
                .attr.progressBarStyleHorizontal);

        Drawable redStyle = ContextCompat.getDrawable(this, R.drawable.red_progressbar);
        rightProgressBar.setProgressDrawable(redStyle);
        rightProgressBar.setAlpha(0.7f);
        leftProgressBar.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        rightProgressBar.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        leftProgressBar.setPadding(10, 10, 10, 10);

        rightProgressBar.setPadding(10, 10, 10, 10);
        leftProgressBar.setRotation(180);
        gameState.initProgressBar(leftProgressBar, Sprite.Player.LEFT);
        gameState.initProgressBar(rightProgressBar, Sprite.Player.RIGHT);
        LinearLayout progressBarComponent = new LinearLayout(this);
        progressBarComponent.setOrientation(LinearLayout.HORIZONTAL);
        progressBarComponent.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        progressBarComponent.setWeightSum(2);
        progressBarComponent.addView(leftProgressBar);
        TextView leftName = new TextView(this);

        progressBarComponent.addView(rightProgressBar);

        gameComponentsLayout.addView(progressBarComponent);


        //============================== Points ================================

        LinearLayout scoreLayout = new LinearLayout(this);
        scoreLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView pointsTv = new TextView(this);

        pointsTv.setTextSize(50);
        pointsTv.setTextColor(Color.rgb(255, 215, 0));
        RelativeLayout.LayoutParams pointsLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//        pointsLayoutParams.addRule(RelativeLayout.RIGHT_OF, sendBazookaSoldier.getId());
        gameState.initCredits(pointsTv);

        scoreLayout.addView(pointsTv, pointsLayoutParams);
        //   buttons.addView(rightPointsTv);
firstLineLayout.addView(scoreLayout);

        //======================================================================
        //game.addView(gameSurface);
        //game.addView(surfaceFrame);




        //========================

//        setContentView(new GameSurface(this));
        //setContentView(gameComponentsLayout);


        if (isMultiplayer) {
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
        super.onPause();
        if (isMultiplayer) {
            Client.getClientInstance().
                    send(Protocol.stringify(Protocol.Action.PAUSE));
        }
       gameSurface.stopGameLoop();

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
            if (!file.delete()) {
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
        switch (action) {
            case ARROW:
                double arrowDistance = Double.parseDouble(Protocol.getData(rawInput));
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

            case SOLDIER_KILL:
                try {
                    JSONObject data = new JSONObject(Protocol.getData(rawInput));
                    int id = data.getInt("id");
                    String playerString = data.getString("player");
                    //We know that the other player sent the message, thus
                    //we need to flip the players
                    Sprite.Player player =
                            playerString.equals(Sprite.Player.LEFT.toString()) ?
                            Sprite.Player.RIGHT : Sprite.Player.LEFT;
                    this.gameState.removeSoldier(id, player);
                } catch (JSONException e){
                    e.printStackTrace();
                }



                break;

            case MATH_BOMB:
                final MathBomb bomb= new MathBomb(this);
                //disable other buttons
                gameState.disableButtons();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((FrameLayout) findViewById(R.id.center_of_screen)).addView(bomb.getBomb());
                    }
                });
                break;

            case START_GAME:
                this.gameState.setTime(System.currentTimeMillis(),
                        Long.parseLong(Protocol.getData(rawInput)));
                this.waitDialog.dismiss();
                break;

//            case PAUSE:
//                gameSurface.goToMarket();
//                this.gameSurface.sleep();
//                break;

            case RESUME:
                this.gameSurface.wakeUp();
                break;

            case MATH_BOMB_SOLVED:
                for(final Button button: this.buttons){
                   Object tag= button.getTag();
                    if(tag instanceof String && ((String) tag).equals("MathBomb")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_bomb_grayed, 0, 0, 0);

                    }
                });
                    }
                }
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
                                if (!file.delete()) {
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
