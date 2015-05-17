package huji.ac.il.stick_defence;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Nir on 17/05/2015.
 */
public class Server {
    public static final int PORT=6666;
    private static Server server;
    private  boolean running=false;
    private   ArrayList<Peer> Peers = new ArrayList<>();
    private  ServerSocket serverSocket;

    private Server(){}

    public static Server createServer(){
        if(server==null){
            server=new Server();
            server.start();
        }
        return server;
    }

    public static Server getServerInstance(){
        return server;
    }

    private void start(){
        this.running=true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.w("custom", "starting server");
                try {
                    serverSocket = new ServerSocket(PORT);
                    while (running) {

                        Socket socket = serverSocket.accept();
                        Log.w("custom", "client excepted!");
                        Peer peer = new Peer(socket);
                        Peers.add(peer);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private void doAction(String action, String data, Peer peer){
        if(action.equals(Protocol.Action.NAME.toString())){
            peer.setName(data);
            peer.send(Protocol.stringify(Protocol.Action.NAME_CONFIRMED));
        }
    }


    private class ServerSocketListener extends AsyncTask<Peer,Void, Void> {

        @Override
        protected Void doInBackground(Peer[] params) {
            Peer peer= params[0];
            Socket socket = peer.socket;

            Log.w("custom", "start socket listener");
            String inputLine;

            try {
            BufferedReader in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
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

    private class Peer {
        private int id;
        private String name;
        private PrintWriter out;
        private Socket socket;

        public void setName(String name){
            this.name=name;
        }

        public void send(String out){
            this.out.println(out);
        }
        public Peer(Socket socket){

            this.socket=socket;
            new ServerSocketListener().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
