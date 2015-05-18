package huji.ac.il.stick_defence;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class represents a server that manages all the league stuff
 * It uses the singleton pattern so that the current instance of the server doesn't depend on the calling
 * activity and his life cycle is during the whole league.
 */
public class Server {
    public static final int PORT=6666; //arbitrary port
    private static Server server;
    private  boolean running=false;
    private   ArrayList<Peer> Peers = new ArrayList<>(); //we keep tracking all the connected peers
    private  ServerSocket serverSocket;

    private Server(){} //private constructor, for the singleton pattern.

    /**
     * Creates a new server if the current static server is null
     * @return an instance of the server
     */
    public static Server createServer(){
        if(server==null){
            server=new Server();
            server.start();
        }
        return server;
    }

    /**
     * Returns an instance of the server. Could return null if no one called "createServer" before
     *
     * @return an instance of the server.
     */
    public static Server getServerInstance(){
        return server;
    }

    /**
     * Start the server.
     */
    private void start(){
        this.running=true;
        //we create a new socket and listen to it on a different thread:
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.w("custom", "starting server");
                try {
                    serverSocket = new ServerSocket(PORT);
                    while (running) {
                        Socket socket = serverSocket.accept(); //the accept method is blocking.
                        Log.w("custom", "client excepted!"); //if we reach this line only when a new client is connected.
                        Peer peer = new Peer(socket);
                        Peers.add(peer); //save the new client in the peers list
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /**
     * This method decide what to do on each data received from a clent.
     * @param action the action received from the client
     * @param data the data received from the client
     * @param peer the clent that send us the action.
     */
    private void doAction(String action, String data, Peer peer){
        // if the client sends us his name, we saved the name and send confirmation.
        if(action.equals(Protocol.Action.NAME.toString())){
            peer.setName(data);
            peer.send(Protocol.stringify(Protocol.Action.NAME_CONFIRMED));
        }
    }

    /**
     * This class represents a socket listener on a client node.
     */
    private class ServerSocketListener extends AsyncTask<Peer,Void, Void> {

        @Override
        protected Void doInBackground(Peer[] params) {
            Peer peer= params[0];
            Socket socket = peer.socket;

            Log.w("custom", "start socket listener");
            String inputLine;

            try {
            BufferedReader in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((inputLine = in.readLine()) != null) { //the readLine is a blocking method.
                    Log.w("custom", inputLine);
                    String[] action= Protocol.parse(inputLine);
                    doAction(action[0],action[1],peer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.w("custom", "finish socket listener");



            return null;
        };
    }

    /**
     * A socket wrapper.
     * Instead of keeping track on raw sockets, we wrap them as peers with name, id, and usfull methods.
     */
    private class Peer {
        private int id; //unique id for each peer
        private String name; //name of the client. don't have to be unique.
        private PrintWriter out;
        private Socket socket;

        /**
         * Constructs a new peer.
         * @param socket the socket to wrap
         */
            public Peer(Socket socket){

            this.socket=socket;
            new ServerSocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * Sets the name of the client this peer belongs too.
         * @param name the name of the client.
         */
        public void setName(String name){
            this.name=name;
        }

        /**
         * Send data to the socket
         * @param out the data to be sent.
         */
        public void send(String out){
            this.out.println(out);
        }


    }

}
