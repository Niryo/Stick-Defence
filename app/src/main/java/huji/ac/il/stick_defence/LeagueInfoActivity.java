package huji.ac.il.stick_defence;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class LeagueInfoActivity extends Activity implements DoProtocolAction {
    private Client client = Client.getClientInstance();
    private ProgressDialog waitDialog;
    private int text_size;
    private int TEXT_SCALE_FACTOR=30;
    private final String BUTTON_PUSHED_COLOR= "#FFFFCC";
    private final String BUTTON_RELEASED_COLOR="#FF9900";
    private String winner=" testWiner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_league_info);
        int screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        this.text_size = screenWidth/TEXT_SCALE_FACTOR;
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Waiting for league information");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        waitDialog.show();
        client.setCurrentActivity(this);

        final Button readyButton = (Button) findViewById(R.id.ready_to_play);
        final boolean newGame = getIntent().getBooleanExtra("NewGame", true);



        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //todo: switch to game and only then send ready to play.
                Intent gameIntent = new Intent(getApplicationContext(),
                        GameActivity.class);
                gameIntent.putExtra("NewGame", newGame);
                startActivity(gameIntent);
                finish();
            }
        });

        readyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    readyButton.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
                    readyButton.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
                    readyButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    readyButton.setTextColor(Color.BLACK);
                    readyButton.setTypeface(Typeface.SERIF);
                    readyButton.setShadowLayer(0, 0, 0, 0);
                    ;
                }
                return false;
            }
        });

        buildTableHead();
        initTabs();

        if (getIntent().hasExtra("info")) {
            waitDialog.dismiss();
            printLeagueInfo(getIntent().getStringExtra("info"));
        }
        if(getIntent().hasExtra("internet")){
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            waitDialog.setMessage("Waiting for league information\n Your ip address is: "+ip);
        }



    }

private void initTabs(){
    final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
    tabHost.setup();
    TabHost.TabSpec ts1= tabHost.newTabSpec("Tab1");
    ts1.setIndicator("Statistics");
    ts1.setContent(R.id.tab1);
    tabHost.addTab(ts1);

    tabHost.setup();
    TabHost.TabSpec ts2= tabHost.newTabSpec("Tab2");
    ts2.setIndicator("Next round battles");
    ts2.setContent(R.id.tab2);
    tabHost.addTab(ts2);

    tabHost.setup();
    TabHost.TabSpec ts3= tabHost.newTabSpec("Tab3");
    ts3.setIndicator("Winner");
    ts3.setContent(R.id.tab3);
    tabHost.addTab(ts3);


    final Button buttonTab1= (Button) findViewById(R.id.buttonTab1);
    final Button buttonTab2= (Button) findViewById(R.id.buttonTab2);

    buttonTab1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tabHost.setCurrentTab(0);
            buttonTab1.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
            buttonTab1.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
            buttonTab1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            buttonTab1.setTypeface(Typeface.SERIF);
            buttonTab2.setTextColor(Color.BLACK);
            buttonTab2.setTypeface(Typeface.SERIF);
            buttonTab2.setShadowLayer(0,0,0,0);
        }
    });

    buttonTab2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tabHost.setCurrentTab(1);
            buttonTab2.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
            buttonTab2.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
            buttonTab2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            buttonTab2.setTypeface(Typeface.SERIF);
            buttonTab1.setTextColor(Color.BLACK);
            buttonTab1.setTypeface(Typeface.SERIF);
            buttonTab1.setShadowLayer(0,0,0,0);
        }
    });
    buttonTab1.callOnClick();
}
    @Override
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);



        switch (action) {
            case LEAGUE_INFO:
                waitDialog.dismiss();
                printLeagueInfo( Protocol.getData(rawInput));
            /* if( GameState.getInstance().getInstance()!=null){
                 GameState.getInstance().getInstance().sendInfoToPartner();
            }*/
                break;
            case PARTNER_INFO:
                GameState.getInstance().newPartnerInfo( Protocol.getData(rawInput));
                break;

        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void printLeagueInfo(String rawInfo) {
        final LinearLayout vsLayout = (LinearLayout) findViewById(R.id.vsLinearLayout);

        try {
            JSONObject info = null;
            info = new JSONObject(rawInfo);
                if (info.has("pairs")) {
                    JSONObject pairs= (JSONObject)info.get("pairs");
                    Iterator<?> pairKeys = pairs.keys();
                    while (pairKeys.hasNext()) {
                        String playerKey = (String) pairKeys.next();
                    JSONObject players = (JSONObject) pairs.get(playerKey);
                    String player1 = players.getString("player1");
                    String player2 = players.getString("player2");
                    final VSview vsView = new VSview(this);
                    LinearLayout.LayoutParams lp=  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1);
                    lp.setMargins(0,0,0,50);
                    vsView.setNames(player1, player2);
                    vsView.setLayoutParams(lp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vsLayout.addView(vsView);

                        }
                    });}

                }
                if(info.has("statistics")) {
                    ArrayList<PlayerWins> dataToSort = new ArrayList<>();
                    final TableLayout table = (TableLayout) findViewById(R.id.statistics_table);
                    JSONObject stats = (JSONObject) info.get("statistics");
                    Iterator<?> statKeys = stats.keys();
                    while (statKeys.hasNext()) {
                        String name = (String) statKeys.next();
                        dataToSort.add(new PlayerWins(name, stats.getInt(name)));
                    }
                    Collections.sort(dataToSort, new Comparator<PlayerWins>() {
                        @Override
                        public int compare(PlayerWins first, PlayerWins second) {
                            return second.wins - first.wins;
                        }
                    });
                    int count = 1;
                    this.winner=dataToSort.get(0).name;
                    for (PlayerWins pw : dataToSort) {
                        final TableRow tr = new TableRow(this);
                        TextView place = new TextView(this);
                        //place.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                        place.setText("" + count);
                        place.setTextSize(this.text_size);
                        place.setGravity(Gravity.CENTER);
                        count++;
                        TextView name = new TextView(this);
                        name.setText(pw.name);
                        name.setTextSize(this.text_size);
                        name.setGravity(Gravity.CENTER);
                      //  name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                        TextView wins = new TextView(this);
                        wins.setText("" + pw.wins);
                        wins.setTextSize(this.text_size);
                        wins.setGravity(Gravity.CENTER);
                     //   wins.setBackground(getResources().getDrawable(R.drawable.cell_shape));

                        tr.addView(place);
                        tr.addView(name);
                        tr.addView(wins);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                table.addView(tr);

                            }
                        });

                    }

                }


                if(info.has("end_of_league")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView winText = (TextView) findViewById(R.id.tab3);
                            winText.setText("All glory to " + winner + "!\n The big winner of the league!");
                            winText.setTextSize((float) (text_size * 2));
                            winText.setTextColor(Color.parseColor("#FF9900"));
                            winText.setShadowLayer(10, 0, 0, Color.parseColor("#FFFF66"));
                            final TabHost tabHost= (TabHost) findViewById(R.id.tabHost);


                            final Button buttonTab1= (Button) findViewById(R.id.buttonTab1);
                            buttonTab1.setText("Winner");
                            final Button buttonTab2= (Button) findViewById(R.id.buttonTab2);
                            buttonTab2.setText("Statistics table");

                            buttonTab1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tabHost.setCurrentTab(2);
                                    buttonTab1.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
                                    buttonTab1.setShadowLayer(8, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
                                    buttonTab1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                    buttonTab1.setTypeface(Typeface.SERIF);
                                    buttonTab2.setTextColor(Color.BLACK);
                                    buttonTab2.setTypeface(Typeface.SERIF);
                                    buttonTab2.setShadowLayer(0, 0, 0, 0);
                                }
                            });

                            buttonTab2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tabHost.setCurrentTab(0);
                                    buttonTab2.setTextColor(Color.parseColor(BUTTON_PUSHED_COLOR));
                                    buttonTab2.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
                                    buttonTab2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                    buttonTab2.setTypeface(Typeface.SERIF);
                                    buttonTab1.setTextColor(Color.BLACK);
                                    buttonTab1.setTypeface(Typeface.SERIF);
                                    buttonTab1.setShadowLayer(0, 0, 0, 0);
                                }
                            });
                            buttonTab1.callOnClick();
                            ((Button) findViewById(R.id.ready_to_play)).setText("New league round");

                        }
                    });
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class PlayerWins {
        private String name;
        private int wins;

        PlayerWins(String name, int wins) {
            this.name = name;
            this.wins = wins;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void buildTableHead() {
        final TableLayout table = (TableLayout) findViewById(R.id.statistics_table);
        final TableRow tr = new TableRow(this);
        TextView place = new TextView(this);
        //place.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        place.setText("Place");
        place.setTextSize(this.text_size);
        place.setGravity(Gravity.CENTER);
        place.setTypeface(Typeface.SERIF);
        place.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        place.setTextSize((int) (this.text_size * 1.5));
        TextView name = new TextView(this);
        name.setText("Name");
        name.setTextSize(this.text_size);
        name.setGravity(Gravity.CENTER);
        name.setTypeface(Typeface.SERIF);
        name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        name.setTextSize((int) (this.text_size * 1.5));
     //   name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        TextView wins = new TextView(this);
        wins.setText("Wins");
        wins.setTextSize(this.text_size);
        wins.setGravity(Gravity.CENTER);
        wins.setTypeface(Typeface.SERIF);
        wins.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        wins.setTextSize((int) (this.text_size * 1.5));
     //   wins.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        tr.addView(place);
        tr.addView(name);
        tr.addView(wins);
        table.addView(tr);
    }
}