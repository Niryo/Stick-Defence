package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.net.Socket;


public class MainMenu extends Activity {
    private String name= "test";
    private Client client= Client.createClient(name);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //========================Single player=================================
        Button singlePlayer = (Button) findViewById(R.id.single_player);
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(getApplicationContext(),
                                               GameActivity.class);
                startActivity(gameIntent);
                finish();


            }
        });
        //========================Create League=================================
        Button createLeague= (Button) findViewById(R.id.create_league);
        createLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pManager mManager =(WifiP2pManager) getSystemService(getApplicationContext().WIFI_P2P_SERVICE);
                WifiP2pManager.Channel mChannel = mManager.initialize(getApplicationContext(), getMainLooper(), null);
                mManager.createGroup(mChannel,null);
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
                        Log.w("custom", "groupInfo:");
                        Log.w("custom", info.toString());
                        if (info.groupOwnerAddress != null) {
                            Log.w("custom", info.groupOwnerAddress.getHostAddress());
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        Socket socket = new Socket(info.groupOwnerAddress.getHostAddress(), Server.PORT);
                                        client.setServer(socket);
                                        //todo:switch to leagMode

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                            ;
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
                Intent createLeague= new Intent(getApplicationContext(), SlaveActivity.class);
                startActivity(createLeague);
                finish();
            }
        });


    }

}
