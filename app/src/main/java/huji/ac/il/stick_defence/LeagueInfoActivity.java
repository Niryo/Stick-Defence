package huji.ac.il.stick_defence;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class LeagueInfoActivity extends Activity implements DoProtocolAction {
    private Client client = Client.getClientInstance();
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_league);
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("Waiting for league information");
        waitDialog.setIndeterminate(true);
        waitDialog.setCancelable(false);
        waitDialog.show();
        client.setCurrentActivity(this);
        Button readyButton = (Button) findViewById(R.id.ready_to_play);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //todo: switch to game and only then send ready to play.
                Intent gameIntent = new Intent(getApplicationContext(),
                        GameActivity.class);
                startActivity(gameIntent);
                finish();
            }
        });
        buildTableHead();
        if (getIntent().hasExtra("info")) {
            waitDialog.dismiss();
            printLeageInfo(getIntent().getStringExtra("info"));
        }


    }


    @Override
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);
        String rawInfo = Protocol.getData(rawInput);


        switch (action) {
            case LEAGUE_INFO:
                waitDialog.dismiss();
                printLeageInfo(rawInfo);
                break; //todo: remove the waiting dialog
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void printLeageInfo(String rawInfo) {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.league_info_layout);

        try {
            JSONObject info = null;
            info = new JSONObject(rawInfo);
            Iterator<?> keys = info.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (!key.equals("statistics")) {
                    JSONObject players = (JSONObject) info.get(key);
                    String player1 = players.getString("player1");
                    String player2 = players.getString("player2");
                    final VSview vsView = new VSview(this);
                    vsView.setNames(player1, player2);
                    vsView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.addView(vsView);

                        }
                    });

                } else {
                    ArrayList<PlayerWins> dataToSort = new ArrayList<>();
                    final TableLayout table = (TableLayout) findViewById(R.id.statistics_table);
                    JSONObject stats = (JSONObject) info.get(key);
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

                    for (PlayerWins pw : dataToSort) {
                        final TableRow tr = new TableRow(this);
                        AutoResizeTextView place = new AutoResizeTextView(this);
                        place.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                        place.setText("" + count);
                        place.setGravity(Gravity.CENTER);
                        count++;
                        AutoResizeTextView name = new AutoResizeTextView(this);
                        name.setText(pw.name);
                        name.setGravity(Gravity.CENTER);
                        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                        AutoResizeTextView wins = new AutoResizeTextView(this);
                        wins.setText("" + pw.wins);
                        wins.setGravity(Gravity.CENTER);
                        wins.setBackground(getResources().getDrawable(R.drawable.cell_shape));

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
        AutoResizeTextView place = new AutoResizeTextView(this);
        place.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        place.setText("Place");
        place.setGravity(Gravity.CENTER);
        AutoResizeTextView name = new AutoResizeTextView(this);
        name.setText("Name");
        name.setGravity(Gravity.CENTER);
        name.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        AutoResizeTextView wins = new AutoResizeTextView(this);
        wins.setText("Wins");
        wins.setGravity(Gravity.CENTER);
        wins.setBackground(getResources().getDrawable(R.drawable.cell_shape));
        tr.addView(place);
        tr.addView(name);
        tr.addView(wins);
        table.addView(tr);
    }
}