package huji.ac.il.stick_defence;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a static class that represents a protocol between a client and a server.
 * All the method in this class should be static.
 * <p/>
 * the complete protocol:
 * Stage 1: the client connects to the server and sent it his name.
 * if the server is willing to accept the client it will send a name confirmation.
 * <p/>
 * #client: send NAME
 * #server: send name NAME_CONFIRMED
 * <p/>
 * stage 2: When the server is ready it will send an information about the league to all his clients.
 * <p/>
 * #server: send LEAGUE_INFO
 * <p/>
 * stage 3: clients tell the server that they are ready to play
 * #client: send READY_TO_PLAY
 * <p/>
 * stage 4: server tells the clients start the game and send a time stamp.
 * #server: send START_GAME
 * <p/>
 * stage 5: todo: during game protocol.
 * <p/>
 * stage 6: clients tell the server that game has been finished by sending it GameOver with the winning side
 * #client: send GAME_OVER
 * <p/>
 * stage 7: server send again information about the league
 * #server: send LEAGUE_INFO
 * <p/>
 * stage 8: if league ends, server send END_LEAGUE and a winner is announced.
 * #server: send END_LEAGUE
 */
public class Protocol {
    private Client client;


    private Protocol() {
    }


    /**
     * represents the actions that could be sent during a communication session
     */
    public static enum Action {
        NAME,
        READY_TO_PLAY,
        NAME_CONFIRMED,
        LEAGUE_INFO,
        PREPARE_GAME,
        START_GAME,
        BASIC_SOLDIER,
        BAZOOKA_SOLDIER,
        ARROW,
        LEFT_WIN,
        RIGHT_WIN,
        GAME_OVER,
        PAUSE,
        RESUME,
        END_LEAGUE
    }

    /**
     * Prepare an action without data for being sent over the socket.
     *
     * @param action the action
     * @return a string representation of the action
     */
    public static String stringify(Action action) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("command", action.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject.toString();
    }

    /**
     * Prepare an action with data for being sent over the socket.
     *
     * @param action the action
     * @param data
     * @return a string representation of the action and the data
     */
    public static String stringify(Action action, String data) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("command", action.toString());
            jObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject.toString();
    }

    public static String stringify(Action action, String data, Long timeStamp) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("command", action.toString());
            jObject.put("data", data);
            jObject.put("time", Long.toString(timeStamp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject.toString();
    }

    public static String stringify(Action action, Long timeStamp) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("command", action.toString());
            jObject.put("time", Long.toString(timeStamp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject.toString();
    }

    public static Action getAction(String rawInput) {
        try {
            return Action.valueOf(new JSONObject(rawInput).getString("command"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getData(String rawInput) {
        try {
            JSONObject jsonObject = new JSONObject(rawInput);
            return jsonObject.getString("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getTimeStamp(String rawInput) {
        try {
            JSONObject jsonObject = new JSONObject(rawInput);
            return jsonObject.getLong("timeStamp");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String addTimeStampToRawInput(String rawInput) {
        try {
            JSONObject jsonObject = new JSONObject(rawInput);
            jsonObject.put("timeStamp", System.currentTimeMillis());
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
