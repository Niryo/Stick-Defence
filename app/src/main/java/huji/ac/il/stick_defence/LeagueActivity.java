package huji.ac.il.stick_defence;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class LeagueActivity extends ActionBarActivity implements DoProtocolAction {
    private Client client= Client.getClientInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);
        client.setCurrentActivity(this);
        Button readyButton = (Button) findViewById(R.id.ready_to_play);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send(Protocol.stringify(Protocol.Action.READY_TO_PLAY));
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
