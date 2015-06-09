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

    private int participants;
    private ArrayList<Server.Peer> peers;
    private int stage = 0;
    private int[][] allCombinationWithFour = {{1, 2, 3, 4}, {1, 3, 2, 4}, {1, 4, 2, 3}};
    private int[][] allCombinationWithSix = {{1, 2, 3, 4,5,6}, {1, 3, 2, 5,4,6}, {1, 4, 2, 6,3,5} , {1, 5, 2, 3,4,6}, {1, 6, 2, 4,3,5}};

    public LeagueManager(ArrayList<Server.Peer> peers, int participants) {
        this.peers = peers;
        Collections.shuffle(this.peers);
        this.participants= participants;
    }

    private String twoPersonLeague() {
        Server.getServerInstance().makePair(peers.get(0), peers.get(1));
        String info = ""; //todo: build the string
        return info;
    }

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
    private String sixPersonLeague(){
        String info = ""; //todo: build the string
        for (int i = 0; i < 6; i += 2) {
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i+1];
            Server.getServerInstance().makePair(peers.get(first), peers.get(second));
        }
        return info;
    }

    private String eightPersonLeague() {
        String info = "";
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
        switch(this.participants){
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
