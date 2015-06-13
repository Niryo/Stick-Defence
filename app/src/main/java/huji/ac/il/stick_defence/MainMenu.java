package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Intent;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.Socket;


public class MainMenu extends Activity implements DoProtocolAction {
    private String name = "test";
    private Client client;// = Client.createClient(name);
    private boolean isCreateLeagueOptionsVisible=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        client = Client.createClient(name);
        this.client.setCurrentActivity(this);

     //   checkForOngoingGame();

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
                if (isCreateLeagueOptionsVisible) { //if league options are already visible,
                    isCreateLeagueOptionsVisible = false;
                    findViewById(R.id.create_league_options).setVisibility(View.GONE);
                    return;

                }
                    final int MAX_N_PLAYERS = 8;
                    final int MIN_N_PLAYERS = 2;
                    final int[] nPlayers = {2};
                    final boolean isWifi = true;

                    findViewById(R.id.create_league_options).setVisibility(View.VISIBLE);
                isCreateLeagueOptionsVisible=true;
                    Button increasePlayersButton = (Button) findViewById(R.id.more_players);
                    Button decreasePlayersButton = (Button) findViewById(R.id.less_players);
                    Button startButton = (Button) findViewById(R.id.start);
                    final TextView nPlayersText = (TextView) findViewById(R.id.num_of_players);
                    RadioGroup networkChoice = (RadioGroup) findViewById(R.id.network_choice);
                    networkChoice.check(R.id.wifi);
                    nPlayersText.setText(String.valueOf(nPlayers[0]));

                    increasePlayersButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nPlayers[0] < MAX_N_PLAYERS) {
                                nPlayers[0]++;
                            }
                            nPlayersText.setText(String.valueOf(nPlayers[0]));
                        }
                    });

                    decreasePlayersButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nPlayers[0] > MIN_N_PLAYERS) {
                                nPlayers[0]--;
                            }
                            nPlayersText.setText(String.valueOf(nPlayers[0]));
                        }
                    });


                    startButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Server.createServer(nPlayers[0]);
                        WifiP2pManager mManager = (WifiP2pManager) getSystemService(getApplicationContext().WIFI_P2P_SERVICE);
                        WifiP2pManager.Channel mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
                        mManager.createGroup(mChannel, null);
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


//                        Intent gameIntent = new Intent(getApplicationContext(),
//                                GameActivity.class);
//                        gameIntent.putExtra("nPlayers", nPlayers[0]);
//                        gameIntent.putExtra("isWifi", isWifi);
//                        startActivity(gameIntent);
//                        finish();
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
            Intent intent = new Intent(this, LeagueInfoActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void checkForOngoingGame(){
        final File file = new File(getFilesDir(), GameState.FILE_NAME);
        if (file.exists()){
            Log.w("yahav", "File exists");
            GameState gameState = GameState.CreateGameState(getApplicationContext());
            Intent gameIntent = new Intent(getApplicationContext(),
                    GameActivity.class);

            if (!gameState.isMultiplayer()){
                gameIntent.putExtra("Multiplayer", false);
                gameIntent.putExtra("NewGame", false);
                startActivity(gameIntent);
                finish();
            } else {
                Intent createLeague = new Intent(getApplicationContext(),
                        JoinLeagueActivity.class);
                startActivity(createLeague);
                finish();
            }

        } else {
            Log.w("yahav", "File doesn't exists");
        }
    }

}
