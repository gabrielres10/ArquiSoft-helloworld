import Demo.*;
import com.zeroc.Ice.Current;

public final class CallbackSenderI {

  //@Override
  public void initiateCallback(CallbackReceiverPrx proxy, com.zeroc.Ice.Current current) {
    System.out.println("initiating callback");
    try {
      proxy.callback("");
    } catch (com.zeroc.Ice.LocalException ex) {
      ex.printStackTrace();
    }
  }

  //@Override
  public void shutdown(com.zeroc.Ice.Current current) {
    System.out.println("Shutting down...");
    try {
      current.adapter.getCommunicator().shutdown();
    } catch (com.zeroc.Ice.LocalException ex) {
      ex.printStackTrace();
    }
  }
}
