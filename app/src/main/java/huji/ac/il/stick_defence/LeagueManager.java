package huji.ac.il.stick_defence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Nir on 29/05/2015.
 */
public class LeagueManager {

    private ArrayList<Server.Peer> peers;
    private int stage = 0; //the stage of the league (every round we will do stage++)
    //an hardcoded pattern for the league: every array represents a round. for example, in the second
    //round we can see that peer 1 will be the partner of peer 3, and peer 2 will be the partner of peer 4:
    private int[][] allCombinationWithFour = {{1, 2, 3, 4}, {1, 3, 2, 4}, {1, 4, 2, 3}};
    private int[][] allCombinationWithSix = {{1, 2, 3, 4,5,6}, {1, 3, 2, 5,4,6}, {1, 4, 2, 6,3,5} , {1, 5, 2, 3,4,6}, {1, 6, 2, 4,3,5}};

    public LeagueManager(ArrayList<Server.Peer> peers) {
        this.peers = peers;
        Collections.shuffle(this.peers); //we shuffle the peers so the pairing will be random.

    }

    /**
     * A two person league. It always pair the first peer with the second peer.
     * @return information about the league
     */
    private String twoPersonLeague() {
        Server.getServerInstance().makePair(peers.get(0), peers.get(1));
        String info = ""; //todo: build the string
        return info;
    }

    /**
     * A four person league. every player will play againt all the other players
     * @return information about the league
     */
    private String fourPersonLeague() {
        //todo:shuffle peers
        String info = ""; //todo: build the string
        for (int i = 0; i < 4; i += 2) {
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i+1];
            Server.getServerInstance().makePair(peers.get(first), peers.get(second));
        }
        return info;
    }

    /**
     * A six person league. every player will play againt all the other players
     * @return information about the league
     */
    private String sixPersonLeague(){
        String info = ""; //todo: build the string
        for (int i = 0; i < 6; i += 2) {
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i+1];
            Server.getServerInstance().makePair(peers.get(first), peers.get(second));
        }
        return info;
    }

    /**
     * A classic eight person league.
     * @return
     */
    private String eightPersonLeague() {
        String info = "";
        //we run over the list of peers making a temp list, with all the peers that have the
        //same number of wins. then, we run over the temp list and pair the peers.
        for (int i = 0; i < stage + 1; i++) {
            ArrayList<Server.Peer> temp = new ArrayList<>();
            for (Server.Peer peer : this.peers) {

                if (peer.getWins() == stage) {
                    temp.add(peer);
                }
            }
            for (int j = 0; j < temp.size(); j += 2) {
                Server.getServerInstance().makePair(temp.get(j), temp.get(j + 1));
            }

        }
        return info;
    }

    public String getLeagueInfo(){
        switch(this.peers.size()){
            case 2: return twoPersonLeague();
            case 4: return fourPersonLeague();
            case 6: return sixPersonLeague();
            case 8: return eightPersonLeague();
            default: return "";
        }

    }

    public void updateLeugeStage(){
        this.stage++;
    }

}
