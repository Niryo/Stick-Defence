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
 * The person who starts the server (by creating a new league) is just hosting the server on
 * his machine, but from the server point of view this person his just a regular client like evryone else.
 * In other words the server doesn't know on witch machine it is being hosted.
 */
public class Server {
    private int leagueParticipants; //todo: make it variable and set it in the constructor.
    public static final int PORT=6666; //arbitrary port
    private static Server server;
    private int counter=0;
    private  boolean acceptingNewClients =false;
    private   ArrayList<Peer> peers = new ArrayList<>(); //we keep tracking all the connected peers todo: change to hashmap based on id
    private  ServerSocket serverSocket;
    private boolean test=true;
    private LeagueManager leagueManager;

    /**
     * private constructor, for the singleton pattern.
     * @param participants
     */
    private Server(int participants){
        this.leagueParticipants =  participants;
    }

    /**
     * Creates a new server if the current static server is null
     * @return an instance of the server
     */
    public static Server createServer(int participants){
        if(server==null){
            server=new Server(participants);
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
        this.acceptingNewClients =true;
        //we create a new socket and listen to it on a different thread:
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.w("custom", "starting server");
                try {
                    serverSocket = new ServerSocket(PORT);
                    while (acceptingNewClients) {
                        Socket socket = serverSocket.accept(); //the accept method is blocking.
                        Log.w("custom", "client excepted!"); //if we reach this line only when a new client is connected.
                        Peer peer = new Peer(socket);
                        peers.add(peer); //save the new client in the peers list
                        if(peers.size()== leagueParticipants){ //todo: sleep some time to see that no one is disconnecting
                            acceptingNewClients=false;
                            leagueManager = new LeagueManager(peers, leagueParticipants);
                            String info= leagueManager.getLeagueInfo();
                            sendLeagueInfo(info);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

private void sendLeagueInfo(String info){
    for (Peer peer : this.peers){
        peer.send(Protocol.stringify(Protocol.Action.LEAGUE_INFO, info));
    }
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
            peer.approved=true;
            peer.setName(data);
            peer.send(Protocol.stringify(Protocol.Action.NAME_CONFIRMED));
        }

        if(action.equals(Protocol.Action.READY_TO_PLAY.toString())){ //todo: this is just for testing! need to be removed
//            if(test) {
//                test=false;
//                makePair(peers.get(0), peers.get(1));
//                String currentTime= Long.toString(System.currentTimeMillis());
//                peers.get(0).send(Protocol.stringify(Protocol.Action.START_GAME, currentTime));
//                peers.get(1).send(Protocol.stringify(Protocol.Action.START_GAME, currentTime));
//            }

            peer.readyToPlay=true;
            if(peer.partner.readyToPlay){
                String currentTime= Long.toString(System.currentTimeMillis());
                peer.send(Protocol.stringify(Protocol.Action.START_GAME, currentTime));
                peer.partner.send(Protocol.stringify(Protocol.Action.START_GAME, currentTime));
                //clear the readyToPlayFlag for the next time:
                peer.readyToPlay=false;
                peer.partner.readyToPlay=false;
            }



        }

    }

    public void makePair(Peer peer1, Peer peer2){
        peer1.setPartner(peer2);
        peer2.setPartner(peer1);
    }
    private void destroyPair(Peer peer1, Peer peer2){
        peer1.setPartner(null);
        peer2.setPartner(null);
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
                    if (peer.partner!=null){
                        inputLine+="#"+System.currentTimeMillis();//add time stamp to the action;
                        peer.partner.send(inputLine);
                    }
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
    public class Peer {
        private long WAIT_FOR_APPROVE = 3000;
        private boolean approved=false; //check if the peer is an approved
        private int id; //unique id for each peer
        private String name; //name of the client. don't have to be unique.
        private PrintWriter out;
        private Socket socket;
        private Peer partner=null;
        private int score=0;
        private int wins=0;
        private Boolean readyToPlay=false;
        /**
         * Constructs a new peer.
         * @param socket the socket to wrap
         */
            public Peer(Socket socket){
                //start an asyncTask that will remove this peer from the peers list if it isn't approved:
                new AsyncTask<Peer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Peer... params) {
                        try {
                            Peer currentPeer=params[0];
                            Thread.sleep(WAIT_FOR_APPROVE);
                            if(!approved){
                                peers.remove(currentPeer);
                                currentPeer.socket.close();
                                Log.w("custom", "illegal peer removed!");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);

            this.id = counter++;
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

        /**
         * Return the peer socket
         * @return the peer socket
         */
        public Socket getSocket(){
            return this.socket;
        }

        public void setPartner(Peer peer){
            this.partner=peer;
        }
        public int getScore(){
            return this.score;
        }
        public int getWins(){return this.wins;}
    }

}
