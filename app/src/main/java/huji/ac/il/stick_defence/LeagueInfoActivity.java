package huji.ac.il.stick_defence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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


    private void printLeageInfo(String rawInfo) {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.league_info_layout);

        try {
            JSONObject info = null;
            info = new JSONObject(rawInfo);
            Iterator<?> keys = info.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (!key.equals("statistics")) {
                    JSONObject players = new JSONObject((String) info.get(key));
                    String player1 = players.getString("player1");
                    String player2 = players.getString("player2");
                    final TextView textView = new TextView(this);
                    textView.setText(player1 + " VS " + player2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.addView(textView);

                        }
                    });

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}