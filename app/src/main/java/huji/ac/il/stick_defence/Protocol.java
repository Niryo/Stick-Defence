package huji.ac.il.stick_defence;


/**
 * This is a static class that represents a protocol between a client and a server.
 * All the method in this class should be static.
 *
 * the complete protocol:
 * Stage 1: the client connects to the server and sent it his name.
 * if the server is willing to accept the client it will send a name confirmation.
 *
 * #client: send NAME
 * #server: send name NAME_CONFIRMED
 *
 * stage 2: When the server is ready it will send an information about the league to all his clients.
 *
 * #server: send LEAGUE_INFO
 *
 * stage 3: clients tell the server that they are ready to play
 * #client: send READY_TO_PLAY
 *
 *stage 4: server tells the clients start the game and send a time stamp.
 * #server: send START_GAME
 *
 * stage 5: todo: during game protocol.
 *
 * stage 6: clients tell the server that game has been finished by sending it the game results
 * #client: send GAME_RESUTLS
 *
 * stage 7: server send again information about the league
 * #server: send LEAGUE_INFO
 */
public class Protocol {
   Client client;

    private Protocol(){}

    /**
     * represents the actions that could be sent during a communication session
     */
public static enum Action {
    NAME,
    READY_TO_PLAY,
    GAME_RESUTLS,
    NAME_CONFIRMED,
    LEAGUE_INFO,
    PREPARE_GAME,
    START_GAME,
        SOLDIER,
        ARROW,
}

    /**
     * Prepare an action without data for being sent over the socket.
     * @param action the action
     * @return a string representation of the action
     */
    public static String stringify(Action action){
        return action.toString();
    }

    /**
     * Prepare an action with data for being sent over the socket.
     * @param action the action
     * @param data
     * @return a string representation of the action and the data
     */
    public static String stringify(Action action, String data){
        return action.toString()+"#"+data;
    }

    /**
     * Parse a received action.
     * @param line the received action.
     * @return an array with the action and the data (if there is data)
     */
    public static String[] parse(String line){
        String[] splited= line.split("#");
        String action= splited[0];
        String data= "";
            if(splited.length>1){
                data=splited[1];
            }
        String[] result = {action,data};
        return result;
    }

}
