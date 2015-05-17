package huji.ac.il.stick_defence;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Nir on 17/05/2015.
 */
public class SocketListener extends AsyncTask<Socket,Void, Void> {
    public SocketListener(){

    }

    @Override
    protected Void doInBackground(Socket[] params) {
        Socket socket= params[0];
        Log.w("custom", "start socket listener");
        String inputLine;

        try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                Log.w("custom", inputLine);
                // out.println("client says hi");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.w("custom", "finish socket listener");



        return null;
    };

}
