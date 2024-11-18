import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import Demo.*;

/**
 * This class is used to handle the communication between the clients and the server.
 * This class contains a list of the registered clients, which are all proxies of ChatClient.
 * Any method ran for a client proxy will be executed in the machine where the proxy was created.
 */
public class Server {
    private static int clientCount = 0;
    private static Map<String, ChatClientPrx> registeredClients = new HashMap<>();

    public static void main(String[] args) {

        // This try block is used to initialize the connection with the server by using the information in the server.cfg file.
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg")) {
            
            // This line is used to create the adapter for the server. The adapter is used to create the proxy for the server.
            // Again, this name can be anything as long as it matches with the name used in the server.cfg file
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Service");
            com.zeroc.Ice.Object object = new ChatServerI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimpleChat"));
            adapter.activate();

            System.out.println("Server online. Waiting for clients...");
            communicator.waitForShutdown();
        }
    }

    /**
     * This method is used to register a client in the server. The client is identified by its hostname.
     */
    public static synchronized void registerClient(String hostname, ChatClientPrx client) {
        clientCount++;
        registeredClients.put(hostname + clientCount, client);
        System.out.println(hostname + " has joined! Clients connected to the chat: " + clientCount);
    }

    /**
     * This method is used to unregister a client from the server. The client is identified by its hostname.
     */
    public static synchronized void unregisterClient(String hostname) {
        clientCount--;
        registeredClients.remove(hostname);
        System.out.println("Client disconnected. Total clients: " + clientCount);
    }

    /**
     * This method is used to get the number of clients connected to the server.
     */
    public static synchronized int getClientCount() {
        return clientCount;
    }

    /**
     * This method is used to get a client proxy by its hostname.
     */
    public static synchronized ChatClientPrx getClient(String hostname) {
        String key = null;
        List<String> keysList = new ArrayList<>(registeredClients.keySet());
        for (String element : keysList) {
            // Agregar el elemento actual a la cadena resultante
            if(element.contains(hostname)){
                key = element;
                break;
            }
        }
        if(key != null)
            return registeredClients.get(key);
        else
            return null;
    }

    /**
     * This method is used to get all the clients connected to the server.
     */
    public static synchronized ArrayList<ChatClientPrx> getAllChatClients(){
        return new ArrayList<>(registeredClients.values());
    }
    

}