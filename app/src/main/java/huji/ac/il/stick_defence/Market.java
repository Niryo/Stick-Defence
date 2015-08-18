package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


public class Market extends Activity implements DoProtocolAction {
//TODO: CREATE A BUTTON THAT MOVES YOU INTO LEAGUE_INFO ACTIVITY AND IF THERE IS INFO, SEND EXTRA IN THE INTENT
    private static final int    SWORDMAN_BUY_PRICE = 50;
    private static final int    TANK_BUY_PRICE = 100;
    private static final int    BAZOOKA_BUY_PRICE = 100; // TODO - change to 1000
    private static final int    MATH_BOMB_PRICE = 100;
    private static final String CREDITS = "Credits: ";

    private String              savedLeagueInfo=null;
    private GameState           gameState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Client.getClientInstance().setCurrentActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        final boolean isMultiplayer = getIntent().getBooleanExtra("Multiplayer", true);
        if (isMultiplayer) {
            Client.getClientInstance().
                    send(Protocol.stringify(Protocol.Action.GAME_OVER,
                         String.
                           valueOf(GameState.getInstance().isLeftPlayerWin())));
        }
        Button continueButton = (Button) findViewById(R.id.market_play_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMultiplayer) {
                    Intent intent = new Intent(getApplicationContext(),
                            GameActivity.class);
                    intent.putExtra("Multiplayer", isMultiplayer);
                    intent.putExtra("NewGame", true);
                    startActivity(intent);
                    finish();
                }
                else{ //go to league info
                    Intent intent = new Intent(getApplicationContext(),
                            LeagueInfoActivity.class);
                    if(savedLeagueInfo!= null){
                        intent.putExtra("info", savedLeagueInfo);
                    }
                    startActivity(intent);
                    finish();
                }

            }
        });

        gameState = GameState.getInstance();

        //Show credits
        int credits = gameState.getCredits(Sprite.Player.LEFT);
        final TextView creditsTv = (TextView) findViewById(R.id.market_credits_tv);
        creditsTv.setText(CREDITS + credits + "$");

        //Add buy buttons
        addButton(PlayerStorage.PurchasesEnum.BAZOOKA_SOLDIER,
                  R.id.buy_bazooka_soldier,
                  BAZOOKA_BUY_PRICE);
        addButton(PlayerStorage.PurchasesEnum.SWORDMAN,
                  R.id.buy_swordman,
                  SWORDMAN_BUY_PRICE);
        addButton(PlayerStorage.PurchasesEnum.TANK,
                  R.id.buy_tank,
                  TANK_BUY_PRICE);
        addButton(PlayerStorage.PurchasesEnum.MATH_BOMB,
                  R.id.buy_math_bomb,
                  MATH_BOMB_PRICE);


    }

    private void addButton(final PlayerStorage.PurchasesEnum item,
                           int iconId,
                           final int price){
        Button buyButton = (Button) findViewById(iconId);
        final TextView creditsTv = (TextView) findViewById(R.id.market_credits_tv);
        if (gameState.isPurchased(item)) {
            buyButton.setVisibility(View.INVISIBLE);
        } else {
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int credits = gameState.getCredits(Sprite.Player.LEFT);
                    if (credits >= price){
                        gameState.
                                buySoldier(item,
                                        price);
                        credits -= price;
                        creditsTv.setText(CREDITS + credits + "$");
                        gameState.save();
                        v.setVisibility(View.INVISIBLE);
                    }
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
            File file = new File(getFilesDir(), PlayerStorage.FILE_NAME);
            if (!file.delete()) {
                Log.w("yahav", "Failed to delete file");
            }
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

    @Override
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);
        String rawInfo = Protocol.getData(rawInput);


        switch (action) {
            case LEAGUE_INFO:
                savedLeagueInfo = Protocol.getData(rawInput);
                break;
        }
    }
}
