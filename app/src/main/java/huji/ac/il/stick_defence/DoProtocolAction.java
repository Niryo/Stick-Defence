package huji.ac.il.stick_defence;

/**
 * An interface to doAction.
 */
public interface DoProtocolAction {

    /**
     * Send/Receive message to/from server
     * @param rawInput the message
     */
    void doAction(String rawInput);

}
