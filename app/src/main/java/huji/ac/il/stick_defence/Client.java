package huji.ac.il.stick_defence;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Nir on 15/05/2015.
 */
public class Client {
    private PrintWriter out;
    private String name;
    private Activity currentActivity;
    private static Client client;


    public static Client createClient(String name){
        if(client==null){
            client=new Client(name);
        }
        return client;
    }

    public static Client getClientInstance(){
        return client;
    }

    private Client(String name){
        this.name=name;
    }

public void send(String out){
    this.out.println(out);
}
    public void setServer(Socket server){
        try {
            this.out = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ClientSocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, server);

        send(Protocol.stringify(Protocol.Action.NAME, name));


    }
public void setCurrentActivity(Activity activity){
    this.currentActivity=activity;

}





    private void doAction(String action, String data){
        if(action.equals(Protocol.Action.NAME_CONFIRMED.toString())){
        }
    }

public void switchToLeague(){
    ((SlaveActivity)this.currentActivity).switchToLeagueActivity();
}

    private class ClientSocketListener extends AsyncTask<Socket,Void, Void> {

        @Override
        protected Void doInBackground(Socket...params) {
            Socket server= params[0];
            Log.w("custom", "start socket listener");
            String inputLine;
            try {
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    Log.w("custom", inputLine);
                    String[] action= Protocol.parse(inputLine);
                    doAction(action[0], action[1]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.w("custom", "finish socket listener");
            return null;
        };
    }


}
