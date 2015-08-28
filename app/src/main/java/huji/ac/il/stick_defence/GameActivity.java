package huji.ac.il.stick_defence;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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
    private LinearLayout buttonsLayout;


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

        boolean newGame = true;
        Bundle bundle = getIntent().getExtras();
        newGame = bundle.getBoolean("NewGame", true);
        isMultiplayer = getIntent().getBooleanExtra("Multiplayer", true);

        gameState = GameState.getInstance();
        if (null == gameState){
            this.gameState =
                    GameState.CreateGameState(getApplicationContext(),
                                              this, screenWidth,
                                              newScreenHeight, isMultiplayer);
        } else {
            GameState.getInstance().reset(newGame, isMultiplayer);
        }

        gameComponentsLayout = (LinearLayout) findViewById(R.id.game_components);


        Client.getClientInstance().setCurrentActivity(this);
        gameSurface = new GameSurface(this);
        FrameLayout surfaceFrame = (FrameLayout) findViewById(R.id.canvas_frame);
        ViewGroup.LayoutParams params = surfaceFrame.getLayoutParams();
        params.height = newScreenHeight;
        surfaceFrame.setLayoutParams(params);
        surfaceFrame.addView(gameSurface);


        //======================Add Buttons===========================
        buttonsLayout = new LinearLayout(this);
        //buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout firstLineLayout = new LinearLayout(this);
        firstLineLayout.setOrientation(LinearLayout.HORIZONTAL);

        this.buttons= new ArrayList<>();

        addButton(PlayerStorage.PurchasesEnum.BASIC_SOLDIER,
                  R.drawable.basic_soldier_icon, Protocol.Action.BASIC_SOLDIER,
                GameState.MILLISEC_TO_BASIC_SOLDIER);
        addButton(PlayerStorage.PurchasesEnum.ZOMBIE,
                R.drawable.zombie_icon, Protocol.Action.ZOMBIE,
                GameState.MILLISEC_TO_ZOMBIE);
        addButton(PlayerStorage.PurchasesEnum.SWORDMAN,
                  R.drawable.swordman_icon, Protocol.Action.SWORDMAN,
                GameState.MILLISEC_TO_SWORDMAN);
        addButton(PlayerStorage.PurchasesEnum.BOMB_GRANDPA,
                R.drawable.bomb_grandpa_icon, Protocol.Action.BOMB_GRANDPA,
                GameState.MILLISEC_TO_BOMB_GRANDPA);
        addButton(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER,
                  R.drawable.bazooka_icon, Protocol.Action.BAZOOKA_SOLDIER,
                GameState.MILLISEC_TO_BAZOOKA);
        addButton(PlayerStorage.PurchasesEnum.TANK,
                  R.drawable.tank_icon, Protocol.Action.TANK,
                GameState.MILLISEC_TO_TANK);

        //Different because of the second setCompoundDrawablesWithIntrinsicBounds
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


        if(gameState.isPurchased(PlayerStorage.PurchasesEnum.FOG)){
            Button sendFog = new Button(this);
            sendFog.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.fog_icon, 0, 0, 0);
            sendFog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameState.sendFog();
                    v.setEnabled(false);
//                    ((Button) v).setCompoundDrawablesWithIntrinsicBounds(
//                            R.drawable.math_bomb_sent, 0, 0, 0);
                }
            });

            buttonsLayout.addView(sendFog);
            buttons.add(sendFog);
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
        gameState.initCredits(pointsTv, newGame);

        scoreLayout.addView(pointsTv, pointsLayoutParams);
        //   buttons.addView(rightPointsTv);
        firstLineLayout.addView(scoreLayout);

        //======================================================================

        if (isMultiplayer) {
            readyToPlay();
        }
        else{
            Sounds.getInstance().stopTheme(Sounds.MAIN_THEME);
        }
    }

    private void readyToPlay(){
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

    private void addButton(PlayerStorage.PurchasesEnum item,
                           int pic, final Protocol.Action action, final int intervalInMillisec){
        if (gameState.isPurchased(item)){
            final Button buyButton = new Button(this);
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            buyButton.setLayoutParams(buttonParams);
            buyButton.
                    setCompoundDrawablesWithIntrinsicBounds(pic, 0, 0, 0);


            RelativeLayout buttonLayout = new RelativeLayout(this);

            buyButton.setId(R.id.generic_soldier_id);
            buttonLayout.addView(buyButton);

            gameState.activateSendSoldierButton(buyButton, item);

            final ProgressBar progressButton = new ProgressBar(this, null, android.R
                    .attr.progressBarStyleHorizontal);

            RelativeLayout.LayoutParams progressButtonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            progressButtonParams.addRule(RelativeLayout.ALIGN_BOTTOM,
                                         R.id.generic_soldier_id);
            progressButtonParams.addRule(RelativeLayout.ALIGN_LEFT,
                                         R.id.generic_soldier_id);
            progressButtonParams.addRule(RelativeLayout.ALIGN_RIGHT,
                                         R.id.generic_soldier_id);
            progressButton.setLayoutParams(progressButtonParams);
            progressButton.setAlpha(0.8f);
            progressButton.setMax(intervalInMillisec);
            progressButton.setProgress(intervalInMillisec);
            buttonLayout.addView(progressButton);
            progressButton.bringToFront();
            buttonsLayout.addView(buttonLayout);


            buttons.add(buyButton);

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (gameState.addSoldier(Sprite.Player.LEFT, 0, action)){
                    buyButton.setEnabled(false);
                    progressButton.setProgress(0);

                    CountDownTimer mCountDownTimer = new CountDownTimer(intervalInMillisec, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            progressButton.setProgress(intervalInMillisec -
                                    (int)millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            progressButton.setProgress(intervalInMillisec);
                            buyButton.setEnabled(true);
                        }
                    };
                    mCountDownTimer.start();
                }

                }
            });
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
            case ZOMBIE:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Protocol.getTimeStamp(rawInput),
                        Protocol.Action.ZOMBIE);
                break;
            case SWORDMAN:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Protocol.getTimeStamp(rawInput),
                        Protocol.Action.SWORDMAN);
                break;
            case BAZOOKA_SOLDIER:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Protocol.getTimeStamp(rawInput),
                        Protocol.Action.BAZOOKA_SOLDIER);
                break;
            case TANK:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                        Protocol.getTimeStamp(rawInput),
                        Protocol.Action.TANK);
                break;
            case BOMB_GRANDPA:
                this.gameState.addSoldier(Sprite.Player.RIGHT,
                                          Protocol.getTimeStamp(rawInput),
                                          Protocol.Action.BOMB_GRANDPA);
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


            case FOG:
                gameState.addFog();
                break;


            case START_GAME:
                Sounds.getInstance().stopTheme(Sounds.MAIN_THEME);
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
            case PARTNER_INFO:
                gameState.newPartnerInfo(Protocol.getData(rawInput));
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
