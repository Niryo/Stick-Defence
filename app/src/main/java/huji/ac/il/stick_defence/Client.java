package huji.ac.il.stick_defence;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String id;
    private String name; //each client as a name, dosen't have to be unique.
    private PrintWriter out;
    private DoProtocolAction currentActivity;

    private Client() {}

    /**
     * Constructs a new client with the given name
     *
     * @param name the name of the client
     */
    private Client(String name, String id) {
        this.name = name;
        this.id= id;
    }

    /**
     * Creates a new client if the current static client is null
     *
     * @param name the name of the client
     * @return an instance of the client
     */
    public static Client createClient(String name, String id) {
        if (client == null) {
            client = new Client(name,id);
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
        Log.w("custom", "Sending data to server: " + out);
        this.out.println(out);
    }

    /**
     * Set the server the client is currently connected too.
     * The server could be changed during the game if needed so.
     *
     * @param server the current server
     */
    public void setServer(Socket server) {
        Log.w("custom", "Entering setServer");
        try {
            this.out = new PrintWriter(server.getOutputStream(), true);
            //save the output stream of the server.
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new ServerSocketListener(server)).start();
                // on a different thread
        GameState gameState = GameState.getInstance();
        if (gameState != null && GameState.getInstance().isGameInProcces()){
            send(Protocol.stringify(Protocol.Action.RESUME));
        } else {
            JSONObject jsonName= new JSONObject();
            try {
                jsonName.put("name", name);
                jsonName.put("id",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(Protocol.stringify(Protocol.Action.NAME,
                                    jsonName.toString())); //send the
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

    public void reportArrow(double distance) {
        send(Protocol.stringify(Protocol.Action.ARROW, Double.toString
                (distance)));
    }

    public void reportBasicSoldier() {
        send(Protocol.stringify(Protocol.Action.BASIC_SOLDIER));
    }

    public void reportZombie() {
        send(Protocol.stringify(Protocol.Action.ZOMBIE));
    }

    public void reportSwordman(){
        send(Protocol.stringify(Protocol.Action.SWORDMAN));
    }

    public void reportBombGrandpa(){
        send(Protocol.stringify(Protocol.Action.BOMB_GRANDPA));
    }

    public void reportBazookaSoldier(){
        send(Protocol.stringify(Protocol.Action.BAZOOKA_SOLDIER));
    }

    public void reportTank(){
        send(Protocol.stringify(Protocol.Action.TANK));
    }

    public void reportSoldierKill(int soldierId, Sprite.Player player){
        JSONObject data = new JSONObject();
        try{
            data.put("id", soldierId);
            data.put("player", player.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
        send(Protocol.stringify(Protocol.Action.SOLDIER_KILL,
                data.toString()));
    }

    public void reportWin(Sprite.Player player) {
        if (Sprite.Player.LEFT == player) {
            send(Protocol.stringify(Protocol.Action.LEFT_WIN));
        } else {
            send(Protocol.stringify(Protocol.Action.RIGHT_WIN));
        }
    }

    public void reportMathBomb() {
        send(Protocol.stringify(Protocol.Action.MATH_BOMB));
    }
    public void reportFog(){
        send(Protocol.stringify(Protocol.Action.FOG));
    }

    public void reportPotionOfLife(){
        send(Protocol.stringify(Protocol.Action.POTION_OF_LIFE));
    }

    public void changeName(String name){ this.name = name; }

    public void reportFinalRound(){
        send(Protocol.stringify(Protocol.Action.FINAL_ROUND));
    }


    /**
     * This class represents a socket listener on a server node.
     */

    private class ServerSocketListener implements Runnable{
    private Socket serverSocket;
        public ServerSocketListener(Socket serverSocket){
            this.serverSocket= serverSocket;
        }

        @Override
        public void run() {
            Log.w("custom", "start listening to server");
            String inputLine;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader
                        (this.serverSocket.getInputStream()));
                while ((inputLine = in.readLine()) != null) { //the readLine
                    // is a blocking method.
                    Log.w("custom", "server says: " + inputLine);
                    doAction(inputLine);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.w("custom", "finish socket listener");

        }
    }


}
