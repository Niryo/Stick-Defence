package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class LeagueActivity extends Activity implements DoProtocolAction {
    private static int MAX_N_PLAYERS = 8;
    private static int MIN_N_PLAYERS = 2;

    private Client  client   = Client.getClientInstance();
    private int     nPlayers = 2;
    private boolean isWifi = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);
        client.setCurrentActivity(this);
        setListeners();
    }

    private void setListeners(){
        Button increasePlayersButton = (Button) findViewById(R.id.more_players);
        Button decreasePlayersButton = (Button) findViewById(R.id.less_players);
        Button startButton = (Button) findViewById(R.id.start);
        final TextView nPlayersText = (TextView) findViewById(R.id.num_of_players);
        RadioGroup networkChoice = (RadioGroup) findViewById(R.id.network_choice);
        networkChoice.check(R.id.wifi);
        nPlayersText.setText(String.valueOf(nPlayers));

        increasePlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nPlayers < MAX_N_PLAYERS) {
                    nPlayers++;
                }
                nPlayersText.setText(String.valueOf(nPlayers));
            }
        });

        decreasePlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nPlayers > MIN_N_PLAYERS) {
                    nPlayers --;
                }
                nPlayersText.setText(String.valueOf(nPlayers));
            }
        });

        networkChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.wifi == checkedId){
                    isWifi = true;
                } else {
                    isWifi = false;
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //todo: switch to game and only then send ready to play.
                Intent gameIntent = new Intent(getApplicationContext(),
                        GameActivity.class);
                gameIntent.putExtra("nPlayers", nPlayers);
                gameIntent.putExtra("isWifi", isWifi);
                startActivity(gameIntent);
                finish();
            }
        });
    }


    @Override
    public void doAction(String action, String data) {
        if (action.equals(Protocol.Action.START_GAME.toString())) {
            Intent gameIntent = new Intent(getApplicationContext(),
                    GameActivity.class);
            startActivity(gameIntent);
            finish();

        }
    }
}
