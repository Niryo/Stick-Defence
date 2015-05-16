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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String name="Connecting..";
    public Protocol protocol;
    private Activity currentActivity;
    public Client(Socket socket){
        this.socket=socket;
        this.protocol= new Protocol(this);

        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public void setCurrentActivity(Activity activity){
    this.currentActivity=activity;

}
    public void out(String data)
    {
        this.out.println(data);
    }
    public BufferedReader getInStream(){
        return this.in;
    }
    public String getName(){
        return this.name;
    }

    public void startListening(){
        new SocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.socket);
    }
public void switchToLeague(){
    ((SlaveActivity)this.currentActivity).switchToLeagueActivity();
}
public void setName(String name){
    this.name=name;
}
    private class SocketListener extends AsyncTask<Socket,Void, Void> {

        @Override
        protected Void doInBackground(Socket[] params) {
            Socket socket= params[0];
            Log.w("custom", "start socket listener");
            String inputLine;
                String outputLine;
            try {
                while ((inputLine = in.readLine()) != null) {
                    Log.w("custom", inputLine);
                    protocol.parse(inputLine);
                    // out.println("client says hi");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.w("custom", "finish socket listener");



            return null;
        };
    }


}
