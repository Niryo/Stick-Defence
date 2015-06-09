package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Intent;

import java.io.File;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.net.Socket;


public class MainMenu extends Activity implements DoProtocolAction {
    private String name = "test";
    private Client client = Client.createClient(name);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //client = Client.createClient(name);
        this.client.setCurrentActivity(this);
        /*File file = new File(getFilesDir(), GameState.fileName);
        if (file.exists()){
            Log.w("yahav", "File exists");
            GameState gameState = GameState.CreateGameState(getApplicationContext());
            Intent gameIntent = new Intent(getApplicationContext(),
                    GameActivity.class);
            gameIntent.putExtra("Multiplayer", gameState.isMultiplayer());
            gameIntent.putExtra("NewGame", false);
            startActivity(gameIntent);
            finish();
        } else {
            Log.w("yahav", "File doesn't exists");
        }*/

        //========================Single player=================================
        Button singlePlayer = (Button) findViewById(R.id.single_player);
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(getApplicationContext(),
                        GameActivity.class);
                gameIntent.putExtra("Multiplayer", false);
                gameIntent.putExtra("NewGame", true);
                startActivity(gameIntent);
                finish();


            }
        });
        //========================Create League=================================
        Button createLeague = (Button) findViewById(R.id.create_league);
        createLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Server.createServer(2); //todo: change to variable;
                WifiP2pManager mManager =(WifiP2pManager) getSystemService(getApplicationContext().WIFI_P2P_SERVICE);
                WifiP2pManager.Channel mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
                mManager.createGroup(mChannel,null);
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {

                    @Override
                    public void onConnectionInfoAvailable(final WifiP2pInfo
                                                                  info) {
                        Log.w("custom", "groupInfo:");
                        Log.w("custom", info.toString());
                        if (info.groupOwnerAddress != null) {
                            Log.w("custom", info.groupOwnerAddress
                                    .getHostAddress());
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        Socket socket = new Socket(info
                                                .groupOwnerAddress
                                                .getHostAddress(), Server.PORT);
                                        client.setServer(socket);
                                        //todo:switch to leagueMode

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.executeOnExecutor(AsyncTask
                                    .THREAD_POOL_EXECUTOR, null);
                        }

                    }
                });
            }
        });


        //========================Join league=================================
        Button joinLeague = (Button) findViewById(R.id.join_league);
        joinLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createLeague = new Intent(getApplicationContext(),
                        JoinLeagueActivity.class);
                startActivity(createLeague);
                finish();
            }
        });


    }

    @Override
    public void doAction(String action, String data) {
        if (action.equals(Protocol.Action.NAME_CONFIRMED.toString())) {
            //todo: go into leagActivity and wait
            Log.w("custom", "going to league");
            Intent intent = new Intent(this, LeagueActivity.class);
            startActivity(intent);
            finish();
        }


    }

}
