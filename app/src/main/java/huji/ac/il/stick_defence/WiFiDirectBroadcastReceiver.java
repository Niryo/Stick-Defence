package huji.ac.il.stick_defence;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Collection;


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private SlaveActivity activity;

    public WiFiDirectBroadcastReceiver(SlaveActivity activity, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        super();
        this.activity= activity;
        this.mManager = manager;
        this.mChannel = channel;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.w("custom", "wifi enabled");
            } else {
                Log.w("custom", "Wi-Fi Direct is not enabled");
            }

        } else {
            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                if (mManager != null) {
                    mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener(){

                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Collection<WifiP2pDevice> deviceList= peers.getDeviceList();
                            activity.addDevices(deviceList);
                            Log.w("custom", "List of devices:");
                            Log.w("custom", deviceList.toString());

                        }
                    });
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                String thisDeviceName = device.deviceName;
                Log.w("custom", thisDeviceName);
            }
        }
    }
}