package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents the market activity
 */
public class Market extends Activity implements DoProtocolAction {
    private final String BUTTON_PUSHED_COLOR   = "#FFFFCC";
    private final String BUTTON_RELEASED_COLOR ="#FFFFFF";

    public static final int    ZOMBIE_BUY_PRICE = 100;
    public static final int    SWORDMAN_BUY_PRICE = 500;
    public static final int    BOMB_GRANDPA_BUY_PRICE = 800;
    public static final int    TANK_BUY_PRICE = 2000;
    public static final int    BAZOOKA_BUY_PRICE = 1000;
    public static final int    MATH_BOMB_PRICE = 200;
    public static final int    BIG_WOODEN_TOWER_PRICE = 200;
    public static final int    STONE_TOWER_PRICE = 400;
    public static final int    FORTIFIED_TOWER_PRICE = 800;
    public static final int    FOG_PRICE = 100;
    public static final int    POTION_OF_LIFE_PRICE = 100;

    private static final String CREDITS = "Credits: ";
    private Tower.TowerTypes myTowerType;
    private static String savedLeagueInfo = null;
    private GameState gameState;
    AlertDialog.Builder alertDialogBuilder;
    private boolean stopSoundOnPause = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Sounds.getInstance().stopAllSound();
        Sounds.getInstance().playTheme(Sounds.MAIN_THEME);
        Client.getClientInstance().setCurrentActivity(this);
        final boolean isMultiplayer =
                getIntent().getBooleanExtra("isMultiplayer", true);

        Log.w("custom","entering market");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        stopSoundOnPause = true;
        final Button continueButton = (Button) findViewById(R.id.market_play_button);


        alertDialogBuilder = new AlertDialog.Builder(this);
        if (getIntent().hasExtra("info")) {

            savedLeagueInfo = getIntent().getStringExtra("info");
        }

        if(getIntent().hasExtra("internet")){
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            alertDialogBuilder.setTitle("New server created");
            alertDialogBuilder
                    .setMessage("Your ip is: "+ ip+"\nGive your ip address to your friends and ask them to join over lan.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog= alertDialogBuilder.create();
            alertDialog.show();
            TextView title= (TextView) findViewById(R.id.market_welcome_tv);
            title.setText(title.getText() + "   (your ip: " + ip + ")");

        }

        gameState= GameState.getInstance();
        if (isMultiplayer && !(gameState ==null)) { //if gameState is null we are at the first round so don't send game_over
            Client.getClientInstance().
                    send(Protocol.stringify(Protocol.Action.GAME_OVER,
                            String.valueOf(GameState.getInstance().isLeftPlayerWin())));
        }

        if(gameState==null) {
            gameState = GameState.CreateGameState(this, isMultiplayer);
            if (isMultiplayer){
                continueButton.setEnabled(false);
                continueButton.setAlpha(0.7f);
                continueButton.setText("Please wait for other players to join" +
                                       " the league before continuing");
            }

        }
        myTowerType = gameState.getLeftTowerType();



        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    continueButton.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
                    continueButton.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
                    continueButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    continueButton.setTextColor(Color.parseColor(BUTTON_RELEASED_COLOR));
                    continueButton.setTypeface(Typeface.SERIF);
                    continueButton.setShadowLayer(0, 0, 0, 0);
                }
                return false;
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sounds.playSound(Sounds.BUTTON_CLICK, false);
                stopSoundOnPause = false;
                if (!isMultiplayer) {
                    Intent intent = new Intent(getApplicationContext(),
                            GameActivity.class);
                    intent.putExtra("Multiplayer", false);
                    intent.putExtra("NewGame", false);
                    startActivity(intent);
                    finish();
                } else { //go to league info
                    JSONObject info = new JSONObject();
                    try {
                        info.put("tower", myTowerType.name());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gameState.sendInfoToPartner(info);
                    Intent intent = new Intent(getApplicationContext(),
                            LeagueInfoActivity.class);
                    intent.putExtra("NewGame", false);
                    if (savedLeagueInfo != null) {
                        Log.w("custom","goint to leguae form market and sending league info");
                        intent.putExtra("info", savedLeagueInfo);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });


        //Show credits
        int credits = gameState.getCredits();
        final TextView creditsTv = (TextView) findViewById(R.id.market_credits_tv);
        creditsTv.setText(CREDITS + credits + "$");

        //Add buy buttons
        addButton(PlayerStorage.PurchasesEnum.ZOMBIE,
                  R.id.buy_zombie,
                  ZOMBIE_BUY_PRICE,
                  "Zombie",
                  Zombie.info(),
                  R.drawable.zombie_icon);
        addButton(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER,
                  R.id.buy_bazooka_soldier,
                  BAZOOKA_BUY_PRICE,
                  "Bazooka Soldier",
                  BazookaSoldier.info(),
                  R.drawable.bazooka_icon);
        addButton(PlayerStorage.PurchasesEnum.SWORDMAN,
                  R.id.buy_swordman,
                  SWORDMAN_BUY_PRICE,
                  "Swordman",
                  Swordman.info(),
                  R.drawable.swordman_icon);
        addButton(PlayerStorage.PurchasesEnum.BOMB_GRANDPA,
                  R.id.buy_bomb_grandpa,
                  BOMB_GRANDPA_BUY_PRICE,
                  "Bomb Grandpa",
                  BombGrandpa.info(),
                  R.drawable.bomb_grandpa_icon);
        addButton(PlayerStorage.PurchasesEnum.TANK,
                  R.id.buy_tank,
                  TANK_BUY_PRICE,
                  "Tank",
                  Tank.info(),
                  R.drawable.tank_icon);
        addButton(PlayerStorage.PurchasesEnum.POTION_OF_LIFE,
                R.id.buy_potion,
                POTION_OF_LIFE_PRICE,
                "Potion of Life",
                PotionOfLife.info(),
                R.drawable.potion_icon);
        if (isMultiplayer){
            addButton(PlayerStorage.PurchasesEnum.MATH_BOMB,
                    R.id.buy_math_bomb,
                    MATH_BOMB_PRICE,
                    "Math Bomb",
                    MathBomb.info(),
                    R.drawable.math_bomb_grayed);
            addButton(PlayerStorage.PurchasesEnum.FOG,
                      R.id.buy_fog,
                      FOG_PRICE,
                      "Fog",
                      Fog.info(),
                      R.drawable.fog_icon);
        }

        //BigWoodenTower
        final Button bigWoodenTowerButton =
                (Button) findViewById(R.id.buy_big_wooden_tower);
        final Button stoneTowerButton =
                (Button) findViewById(R.id.buy_stone_tower);
        final Button fortifiedTowerButton =
                (Button) findViewById(R.id.buy_fortified_tower);

        if (!gameState.isPurchased(PlayerStorage.PurchasesEnum.BIG_WOODEN_TOWER)){
            bigWoodenTowerButton.setVisibility(View.VISIBLE);
        } else if (!gameState.isPurchased(PlayerStorage.PurchasesEnum.STONE_TOWER)){
            stoneTowerButton.setVisibility(View.VISIBLE);
        } else if (!gameState.isPurchased(PlayerStorage.PurchasesEnum.FORTIFIED_TOWER)){
            fortifiedTowerButton.setVisibility(View.VISIBLE);
        }

        bigWoodenTowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Sounds.playSound(Sounds.BUTTON_CLICK, false);
            alertDialogBuilder.setTitle("Big Wooden Tower");
            alertDialogBuilder
                .setMessage(BigWoodenTower.info())
                .setIcon(R.drawable.big_wooden_tower_blue_icon)
                .setCancelable(false)
                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (gameState.decCredits(BIG_WOODEN_TOWER_PRICE)) {
                            gameState.buyItem(PlayerStorage.PurchasesEnum.BIG_WOODEN_TOWER,
                                    BIG_WOODEN_TOWER_PRICE);
                            creditsTv.setText(CREDITS + gameState.getCredits() + "$");
                            bigWoodenTowerButton.setVisibility(View.INVISIBLE);
                            stoneTowerButton.setVisibility(View.VISIBLE);
                            myTowerType = Tower.TowerTypes.BIG_WOODEN_TOWER;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            }
        });

        //StoneTower
        stoneTowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Sounds.playSound(Sounds.BUTTON_CLICK, false);
            alertDialogBuilder.setTitle("Stone Tower");
            alertDialogBuilder
                .setMessage(StoneTower.info())
                .setCancelable(false)
                .setIcon(R.drawable.stone_tower_blue_icon)
                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (gameState.decCredits(STONE_TOWER_PRICE)) {
                            gameState.buyItem(PlayerStorage.PurchasesEnum.STONE_TOWER,
                                    STONE_TOWER_PRICE);
                            creditsTv.setText(CREDITS + gameState.getCredits() + "$");
                            stoneTowerButton.setVisibility(View.INVISIBLE);
                            fortifiedTowerButton.setVisibility(View.VISIBLE);
                            myTowerType = Tower.TowerTypes.STONE_TOWER;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            }
        });

        //StoneTower
        fortifiedTowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Sounds.playSound(Sounds.BUTTON_CLICK, false);
            alertDialogBuilder.setTitle("Fortified Tower");
            alertDialogBuilder
                .setMessage(FortifiedTower.info())
                .setCancelable(false)
                .setIcon(R.drawable.fortified_tower_icon)
                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (gameState.decCredits(FORTIFIED_TOWER_PRICE)) {
                            gameState.buyItem(PlayerStorage.PurchasesEnum.FORTIFIED_TOWER,
                                    FORTIFIED_TOWER_PRICE);
                            creditsTv.setText(CREDITS + gameState.getCredits() + "$");
                            fortifiedTowerButton.setVisibility(View.INVISIBLE);
                            myTowerType = Tower.TowerTypes.FORTIFIED_TOWER;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            }
        });
    }

    private void addButton(final PlayerStorage.PurchasesEnum item,
                           final int buttonId,
                           final int price,
                           final String title,
                           final String info,
                           final int iconId) {
        final Button buyButton = (Button) findViewById(buttonId);
        final TextView creditsTv = (TextView) findViewById(R.id.market_credits_tv);
        if (gameState.isPurchased(item)) {
            buyButton.setVisibility(View.INVISIBLE);
        } else {
            buyButton.setVisibility(View.VISIBLE);
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sounds.playSound(Sounds.BUTTON_CLICK, false);
                    alertDialogBuilder.setTitle(title);
                    alertDialogBuilder
                            .setMessage(info)
                            .setCancelable(false)
                            .setIcon(iconId)
                            .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (gameState.decCredits(price)) {
                                        gameState.buyItem(item, price);
                                        creditsTv.setText(CREDITS +
                                                gameState.getCredits() + "$");
                                        buyButton.setVisibility(View.INVISIBLE);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market, menu);
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
            stopSoundOnPause = false;
            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit")
                .setMessage("Are you sure you want to quit to main menu?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                stopSoundOnPause = false;
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

    @Override
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);
        if (null == action){
            return;
        }
        switch (action) {
            case LEAGUE_INFO:
                Log.w("custom", "receive leagueinfo in : market.class");
                savedLeagueInfo = Protocol.getData(rawInput);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Button    continueButton =    (Button) findViewById(R.id.market_play_button);
                        continueButton.setEnabled(true);
                        continueButton.setAlpha(1f);
                        continueButton.setText("continue");
                    }
                });
                //gameState.sendInfoToPartner(myTowerType); //we got the leauge info so we know now that we have
                //parnter and we need to send him information
                break;

            case PARTNER_INFO:
                GameState.getInstance().newPartnerInfo( Protocol.getData(rawInput));
                break;

            case FINAL_ROUND:
                gameState.setFinalRound(false);
                break;
        }
    }

    public static String getLeagueInfo(){ return savedLeagueInfo; }

    @Override
    protected void onPause() {
        super.onPause();
        if (stopSoundOnPause){
            Sounds sounds = Sounds.getInstance();
            sounds.stopTheme();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sounds sounds = Sounds.getInstance();
        sounds.playTheme(Sounds.MAIN_THEME);
    }
}
