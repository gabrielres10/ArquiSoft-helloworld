import com.zeroc.Ice.Current;
import Demo.*;

/**
 * This class implements the ChatClient interface defined in the file "CallBackModule.ice"
 */
public final class ChatClientI implements ChatClient {

    /**
     * This method is used to receive a message from the server. This method will be referenced
     * in the server side to send messages to the client.
     * @param msg The message received from the server
     * @param current The current object
     */
    @Override
    public void receiveMessage(String msg, com.zeroc.Ice.Current current)
    {
        System.out.println(msg);
    }
}