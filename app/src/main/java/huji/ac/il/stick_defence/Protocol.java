package huji.ac.il.stick_defence;


/**
 * Created by Nir on 16/05/2015.
 */
public class Protocol {
   Client client;

    public Protocol(){}

    public Protocol(Client client){
        this.client= client;
    }
public static enum Action {
    NAME,
    READY_TO_PLAY,
    GAME_RESUTLS,
    NAME_CONFIRMED,
    LEAGUE_INFO,
    PREPARE_GAME,
    START_GAME,

}

    public static String stringify(Action action){
        return action.toString();
    }

    public static String stringify(Action action, String data){
        return action.toString()+"#"+data;
    }

//    public void parse(String line){
//        String[] splited= line.split("#");
//        String command= splited[0];
//        String data= "";
//            if(splited.length>1){
//                data=splited[1];
//            }
//
//          if(command.equals(Command.NAME.toString())){
//              sendNameConfirmed();
//              this.client.setName(data);
//          }
//        if(command.equals(Command.NAME_CONFIRMED.toString())){
//                client.switchToLeague();
//        }
//
//    }

    public static String[] parse(String line){
        String[] splited= line.split("#");
        String command= splited[0];
        String data= "";
            if(splited.length>1){
                data=splited[1];
            }
        String[] result = {command,data};
        return result;
    }

}
