package huji.ac.il.stick_defence;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/**
 * A client for managing all the communication with the server.
 * It uses the singleton pattern so that it will be accessible from all the
 * different activities.
 */
public class Client implements DoProtocolAction, Serializable{
    private static Client client;
    private String name; //each client as a name, dosen't have to be unique.
    private PrintWriter out;
    private DoProtocolAction currentActivity;

    private Client() {
    }

    /**
     * Constructs a new client with the given name
     *
     * @param name the name of the client
     */
    private Client(String name) {
        this.name = name;
    }

    /**
     * Creates a new client if the current static client is null
     *
     * @param name the name of the client
     * @return an instance of the client
     */
    public static Client createClient(String name) {
        if (client == null) {
            client = new Client(name);
        }
        return client;
    }

    /**
     * Returns an instance of the client. Could return null if no one called
     * "createClient" before
     *
     * @return an instance of the client.
     */
    public static Client getClientInstance() {
        return client;
    }

    /**
     * Sends data to the server
     *
     * @param out the data to send
     */
    public void send(String out) {
        Log.w("yahav", "Sending: " + out);
        this.out.println(out);
    }

    /**
     * Set the server the client is currently connected too.
     * The server could be changed during the game if needed so.
     *
     * @param server the current server
     */
    public void setServer(Socket server) {
        Log.w("yahav", "Entering setServer");
        try {
            this.out = new PrintWriter(server.getOutputStream(), true);
            //save the output stream of the server.
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ClientSocketListener().executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, server); //start listen to the server
                // on a different thread
        GameState gameState = GameState.getInstance();
        if (gameState != null && GameState.getInstance().isGameInProcces()){
            send(Protocol.stringify(Protocol.Action.RESUME));
        } else {
            send(Protocol.stringify(Protocol.Action.NAME, name)); //send the
            // client name to the server.
        }



    }

    /**
     * Sets the current activity so that the client could communicate with it.
     *
     * @param activity the current activity.
     */
    public void setCurrentActivity(DoProtocolAction activity) {
        this.currentActivity = activity;

    }


    public void doAction(String rawInput) {
        this.currentActivity.doAction(rawInput);

    }

    public void reportArrow(int distance) {
        send(Protocol.stringify(Protocol.Action.ARROW, Integer.toString
                (distance)));
    }

    public void reportBasicSoldier() {
        send(Protocol.stringify(Protocol.Action.BASIC_SOLDIER));
    }

    public void reportBazookaSoldier(){
        send(Protocol.stringify(Protocol.Action.BAZOOKA_SOLDIER));
    }

    public void reportWin(Sprite.Player player) {
        if (Sprite.Player.LEFT == player) {
            send(Protocol.stringify(Protocol.Action.LEFT_WIN));
        } else {
            send(Protocol.stringify(Protocol.Action.RIGHT_WIN));
        }
    }


    /**
     * This class represents a socket listener on a server node.
     */
    private class ClientSocketListener extends AsyncTask<Socket, Void, Void> {

        @Override
        protected Void doInBackground(Socket... params) {
            Socket server = params[0];
            Log.w("custom", "start listening to server");
            String inputLine;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader
                        (server.getInputStream()));
                while ((inputLine = in.readLine()) != null) { //the readLine
                // is a blocking method.
                    Log.w("custom", "server says: " + inputLine);
                    doAction(inputLine);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.w("custom", "finish socket listener");
            return null;
        }
    }

}
