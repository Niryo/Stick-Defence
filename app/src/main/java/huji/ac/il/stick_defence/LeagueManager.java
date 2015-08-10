package huji.ac.il.stick_defence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nir on 29/05/2015.
 */
public class LeagueManager {
    //TODO: MAKE OUTER JSONobject CALLED "INFO" SO THAT IT LOOKS LIKE {PAIR: {}, STATISTICS: {}}
    private ArrayList<Server.Peer> peers;
    private int stage = 0; //the stage of the league (every round we will do stage++)
    //an hardcoded pattern for the league: every array represents a round. for example, in the second
    //round we can see that peer 1 will be the partner of peer 3, and peer 2 will be the partner of peer 4:
    private int[][] allCombinationWithFour = {{1, 2, 3, 4}, {1, 3, 2, 4}, {1, 4, 2, 3}};
    private int[][] allCombinationWithSix = {{1, 2, 3, 4, 5, 6}, {1, 3, 2, 5, 4, 6}, {1, 4, 2, 6, 3, 5}, {1, 5, 2, 3, 4, 6}, {1, 6, 2, 4, 3, 5}};

    public LeagueManager(ArrayList<Server.Peer> peers) {
        this.peers = peers;
        Collections.shuffle(this.peers); //we shuffle the peers so the pairing will be random.

    }

    /**
     * A two person league. It always pair the first peer with the second peer.
     *
     * @return information about the league
     */
    private String twoPersonLeague() {
        Server.getServerInstance().makePair(peers.get(0), peers.get(1));
        JSONObject pairs = new JSONObject();
        JSONObject players = new JSONObject();
        try {
            players.put("player1", peers.get(0).getName());
            players.put("player2", peers.get(1).getName());
            pairs.put("pair0", players);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addStatistics(pairs);
        String info = pairs.toString();
        return info;
    }

    /**
     * A four person league. every player will play againt all the other players
     *
     * @return information about the league
     */
    private String fourPersonLeague() {
        JSONObject pairs = new JSONObject();
        int pairCount = 0;
        for (int i = 0; i < 4; i += 2) {
            JSONObject players = new JSONObject();
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i + 1];
            Server.getServerInstance().makePair(peers.get(first), peers.get(second));
            try {
                players.put("player1", peers.get(first).getName());
                players.put("player2", peers.get(second).getName());
                pairs.put("pair" + pairCount, players);
                pairCount++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        addStatistics(pairs);
        String info = pairs.toString();
        return info;
    }

    /**
     * A six person league. every player will play againt all the other players
     *
     * @return information about the league
     */
    private String sixPersonLeague() {
        JSONObject pairs = new JSONObject();
        int pairCount = 0;
        for (int i = 0; i < 6; i += 2) {
            JSONObject players = new JSONObject();
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i + 1];
            Server.getServerInstance().makePair(peers.get(first), peers.get(second));
            try {
                players.put("player1", peers.get(first).getName());
                players.put("player2", peers.get(second).getName());
                pairs.put("pair" + pairCount, players);
                pairCount++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        addStatistics(pairs);
        String info = pairs.toString();
        return info;
    }

    /**
     * A classic eight person league.
     *
     * @return
     */
    private String eightPersonLeague() {
        JSONObject pairs = new JSONObject();
        int pairCount = 0;
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
                JSONObject players = new JSONObject();
                Server.getServerInstance().makePair(temp.get(j), temp.get(j + 1));
                try {
                    players.put("player1", temp.get(j).getName());
                    players.put("player2", temp.get(j + 1).getName());
                    pairs.put("pair" + pairCount, players);
                    pairCount++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        addStatistics(pairs);
        String info = pairs.toString();
        return info;
    }

    public String getLeagueInfo() {
        //TODO: if stage == ? return finish league.
        String info="";

        switch (this.peers.size()) {
            case 2:
                info= twoPersonLeague();
                break;
            case 4:
                info= fourPersonLeague();
                break;
            case 6:
                info= sixPersonLeague();
                break;
            case 8:
                info= eightPersonLeague();
                break;
            default:
                info= "";
        }
        if (true) {
            info= fakeInfoForTesting();
        }
        return info;
    }

    private void addStatistics(JSONObject info) {
        try {
            JSONObject stat = new JSONObject();

            for (Server.Peer peer : this.peers) {
                stat.put(peer.getName(), "" + peer.getWins());
            }
            info.put("statistics", stat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String fakeInfoForTesting() {
        try {
            JSONObject pair = new JSONObject();

            for (int i = 0; i < 8; i++) {
                JSONObject players = new JSONObject();
                players.put("player1", "Nir" + i);
                players.put("player2", "Yahav" + i);
                pair.put("pair" + i, players);
            }
            JSONObject stat = new JSONObject();
            for (int i = 0; i < 8; i++) {
                stat.put("Nir" + i, "" + i);
            }
            pair.put("statistics", stat);
            return pair.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateLeugeStage() {
        this.stage++;
    }

}
