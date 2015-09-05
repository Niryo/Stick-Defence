package huji.ac.il.stick_defence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents the league manager
 */
public class LeagueManager {
    private ArrayList<Server.Peer> peers;
    //the stage of the league (every round we will do stage++)
    private int stage = 0;

    //an hardcoded pattern for the league: every array represents a round.
    //For example, in the second round we can see that peer 1 will be the
    //partner of peer 3, and peer 2 will be the partner of peer 4:
    private int[][] allCombinationWithFour = {{0, 1, 2, 3},
                                              {0, 2, 1, 3},
                                              {0, 3, 1, 2}};

    private int[][] allCombinationWithSix  = {{0, 1, 2, 3, 4, 5},
                                              {0, 2, 1, 4, 3, 5},
                                              {0, 3, 1, 5, 2, 4},
                                              {0, 4, 1, 2, 3, 5},
                                              {0, 5, 1, 3, 2, 4}};

    private static final int TWO_PLAYERS_STAGES = 3;
    private static final int FOUR_PLAYERS_STAGES = 3;
    private static final int SIX_PLAYERS_STAGES = 5;
    private static final int EIGHT_PLAYERS_STAGES = 3;
    public LeagueManager(ArrayList<Server.Peer> peers) {
        this.peers = peers;
        //we shuffle the peers so the pairing will be random.
        Collections.shuffle(this.peers);

    }

    /**
     * A two person league. It always pair the first peer with the second peer.
     *
     * @return information about the league
     */
    private JSONObject twoPersonLeague() {
        GameState gameState = GameState.getInstance();
        JSONObject info = new JSONObject();
        JSONObject pairs = new JSONObject();
        try {
        if(stage == TWO_PLAYERS_STAGES){
            addStatistics(info);
                info.put("end_of_league", true);
                resetLeague();
            return info;
        } else if (stage == TWO_PLAYERS_STAGES - 1){
            gameState.setFinalRound(true);
        }
        Server.getServerInstance().makePair(peers.get(0), peers.get(1));
        JSONObject players = new JSONObject();
            players.put("player1", peers.get(0).getName());
            players.put("player2", peers.get(1).getName());
            pairs.put("pair0", players);
        info.put("pairs", pairs);
        addStatistics(info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * A four person league. every player will play againt all the other players
     *
     * @return information about the league
     */
    private JSONObject fourPersonLeague() {
        GameState gameState = GameState.getInstance();
        JSONObject info = new JSONObject();
        JSONObject pairs = new JSONObject();
            try {
        if(stage == FOUR_PLAYERS_STAGES){
            addStatistics(info);
            info.put("end_of_league", true);
            resetLeague();
            return info;
        } else if (stage == FOUR_PLAYERS_STAGES - 1){
            gameState.setFinalRound(true);
        }
        int pairCount = 0;
        for (int i = 0; i < 4; i += 2) {
            JSONObject players = new JSONObject();
            int first = allCombinationWithFour[stage][i];
            int second = allCombinationWithFour[stage][i + 1];
            Server.getServerInstance().makePair(peers.get(first),
                                                peers.get(second));
                players.put("player1", peers.get(first).getName());
                players.put("player2", peers.get(second).getName());
                pairs.put("pair" + pairCount, players);
                pairCount++;

        }
        info.put("pairs",pairs);
        addStatistics(info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return info;
    }

    /**
     * A six person league. every player will play against all the other players
     *
     * @return information about the league
     */
    private JSONObject sixPersonLeague() {
        GameState gameState = GameState.getInstance();
        JSONObject info = new JSONObject();
        JSONObject pairs = new JSONObject();
            try {
        if(stage == SIX_PLAYERS_STAGES){
            addStatistics(pairs);
            info.put("end_of_league", true);
            resetLeague();
            return info;
        } else if (stage == SIX_PLAYERS_STAGES - 1){
            gameState.setFinalRound(true);
        }
        int pairCount = 0;
        for (int i = 0; i < 6; i += 2) {
            JSONObject players = new JSONObject();
            int first = allCombinationWithSix[stage][i];
            int second = allCombinationWithSix[stage][i + 1];
            Server.getServerInstance().makePair(peers.get(first),
                                                peers.get(second));
                players.put("player1", peers.get(first).getName());
                players.put("player2", peers.get(second).getName());
                pairs.put("pair" + pairCount, players);
                pairCount++;
        }

                info.put("pairs", pairs);
        addStatistics(info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return info;
    }

    /**
     * A classic eight person league.
     *
     * @return information about the league
     */
    private JSONObject eightPersonLeague() {
        GameState gameState = GameState.getInstance();
        JSONObject info = new JSONObject();
        JSONObject pairs = new JSONObject();
                try {
        if(stage == EIGHT_PLAYERS_STAGES){
            addStatistics(pairs);
                info.put("end_of_league", true);
            resetLeague();
            return info;
        } else if (stage == EIGHT_PLAYERS_STAGES - 1){
            gameState.setFinalRound(true);
        }
        int pairCount = 0;
        //we run over the list of peers making a temp list,
        //with all the peers that have the same number of wins.
        //Then, we run over the temp list and pair the peers.
        for (int i = 0; i < stage + 1; i++) {
            ArrayList<Server.Peer> temp = new ArrayList<>();
            for (Server.Peer peer : this.peers) {

                if (peer.getWins() == i) {
                    temp.add(peer);
                }
            }
            for (int j = 0; j < temp.size(); j += 2) {
                JSONObject players = new JSONObject();
                Server.getServerInstance().makePair(temp.get(j), temp.get(j + 1));
                    players.put("player1", temp.get(j).getName());
                    players.put("player2", temp.get(j + 1).getName());
                    pairs.put("pair" + pairCount, players);
                    pairCount++;
            }

        }
                    info.put("pairs",pairs);
        addStatistics(info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        return info;
    }

    public JSONObject getLeagueInfo() {
        JSONObject info=null;

        switch (this.peers.size()) {
            case 2:
                info = twoPersonLeague();
                break;
            case 4:
                info = fourPersonLeague();
                break;
            case 6:
                info = sixPersonLeague();
                break;
            case 8:
                info = eightPersonLeague();
                break;
        }

        return info;
    }

    private void resetLeague(){
        this.stage = -1;
        for (Server.Peer peer : peers){
            peer.resetWins();
        }
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



    public void updateLeagueStage() {
        this.stage++;
    }

}
