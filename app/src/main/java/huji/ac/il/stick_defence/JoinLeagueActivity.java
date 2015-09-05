package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents joining to a league activity
 */
public class JoinLeagueActivity extends Activity implements DoProtocolAction {
    private final int TIME_TO_REFRESH_PEERS = 20000;
    private final String BUTTON_PUSHED_COLOR= "#FFFFCC";
    private final String BUTTON_RELEASED_COLOR="#000000";
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private ArrayAdapter adapter;
    private ArrayList<WifiP2pDevice> devices = new ArrayList<>();
    private Client client = Client.getClientInstance();
    private boolean running;
    private boolean stopSoundOnPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_join_league);
        client.setCurrentActivity(this);

        stopSoundOnPause = true;
        final Button exitToMainMenuButton =
                (Button) findViewById(R.id.exit_to_main_menu);

        exitToMainMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    exitToMainMenuButton.setTextColor(Color.parseColor
                            (BUTTON_PUSHED_COLOR));
                    exitToMainMenuButton.setShadowLayer(4, 0, 0, Color.parseColor(BUTTON_RELEASED_COLOR));
                    exitToMainMenuButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    exitToMainMenuButton.setTextColor(Color.parseColor(BUTTON_RELEASED_COLOR));
                    exitToMainMenuButton.setTypeface(Typeface.SERIF);
                    exitToMainMenuButton.setShadowLayer(0, 0, 0, 0);
                }
                return false;
            }
        });
        exitToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSoundOnPause = false;
                Intent mainMenuIntent = new Intent(getApplicationContext(),
                        MainMenu.class);
                startActivity(mainMenuIntent);
                finish();
            }
        });

        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(this, mManager, mChannel);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        ListView list = (ListView) findViewById(R.id.devices);

//Start discovering peers in the background:
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                running = true;
                while (running) {
                    Log.w("custom", "searching peers");
                    mManager.discoverPeers(mChannel,
                            new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onFailure(int reason) {
                            Log.w("custom", "fail searching peers " + reason);
                        }
                    });
                    try {
                        Thread.sleep(TIME_TO_REFRESH_PEERS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);


        adapter = new DeviceAdapter(this,
                                android.R.layout.simple_list_item_1, devices);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                WifiP2pDevice device = devices.get(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                mManager.connect(mChannel, config,
                                 new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.w("custom", "connections success!");
                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {

                            @Override
                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {
                                Log.w("custom", "groupInfo:");
                                Log.w("custom", info.toString());
                                if (info.groupOwnerAddress != null) {
                                    Log.w("custom", info.groupOwnerAddress.
                                                    getHostAddress());
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                Socket socket =
                                                        new Socket(info.
                                                                groupOwnerAddress.
                                                                getHostAddress(),
                                                                Server.PORT);
                                                client.setServer(socket);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                            (Void) null);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.w("custom", "connections failed!"+ " "+ reason);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        Sounds sounds = Sounds.getInstance();
        sounds.playTheme(Sounds.MAIN_THEME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        if (stopSoundOnPause){
            Sounds sounds = Sounds.getInstance();
            sounds.stopTheme();
        }
    }

    public void addDevices(Collection<WifiP2pDevice> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void doAction(String rawInput) {
        Protocol.Action action = Protocol.getAction(rawInput);
        if (null == action){
            return;
        }
        switch (action) {
            case NAME_CONFIRMED:
                running = false;
                Log.w("custom", "going to league");
                stopSoundOnPause = false;
                Intent intent = new Intent(this, LeagueInfoActivity.class);
                startActivity(intent);
                finish();
                break;

            case LEAGUE_INFO:
                Log.w("custom", "going to league");
                stopSoundOnPause = false;
                Intent intentWithInfo = new Intent(this, LeagueInfoActivity.class);
                String info = Protocol.getData(rawInput);
                intentWithInfo.putExtra("info", info);
                startActivity(intentWithInfo);
                finish();
                break;

            case TEST:
              Client.getClientInstance().send(Protocol.stringify(Protocol.Action.TEST));
                break;
        }
    }

    //=============================Adapter class================================
    private class DeviceAdapter extends ArrayAdapter<WifiP2pDevice> {
        public DeviceAdapter(Context context, int resource,
                             ArrayList<WifiP2pDevice> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                convertView = vi.inflate(android.R.layout.simple_list_item_1,
                                         null);
            }

            String title = getItem(position).deviceName;
            TextView titleView = (TextView)
                    convertView.findViewById(android.R.id.text1);
            titleView.setText(title);
            return convertView;
        }
    }
}
