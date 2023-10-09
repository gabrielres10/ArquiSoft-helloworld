import com.zeroc.Ice.Current;
import Demo.*;

public final class CallbackReceiverI implements CallbackReceiver {

    @Override
    public void callback(String msg, com.zeroc.Ice.Current current)
    {
        System.out.println("(Nuevo mensaje) \n" + msg);
    }
}