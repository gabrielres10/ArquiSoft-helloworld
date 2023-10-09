import java.util.HashMap;
import java.util.Map;
import Demo.*;

public class Server {
    private static int clientCount = 0;
    private static Map<String, CallbackReceiverPrx> registeredClients = new HashMap<>();

    public static void main(String[] args) {
        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "server.cfg")) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Service");
            com.zeroc.Ice.Object object = new PrinterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            adapter.activate();

            System.out.println("Server online. Waiting for clients...");
            communicator.waitForShutdown();
        }
    }


    // Método para incrementar el contador de clientes conectados
    public static synchronized void registerClient(String hostname, CallbackReceiverPrx callback) {
        registeredClients.put(hostname, callback);
        clientCount++;
        //System.out.println("Client connected. Total: " + clientCount);
    }

    public static synchronized void unregisterClient(String hostname) {
        clientCount--;
        registeredClients.remove(hostname);
        System.out.println("Client disconnected. Total clients: " + clientCount);
    }

    // Método para obtener el número actual de clientes conectados
    public static synchronized int getClientCount() {
        return clientCount;
    }

    // Método para obtener un cliente registrado por su hostname
    public static synchronized CallbackReceiverPrx getClient(String hostname) {
        String key = null;
        for (Map.Entry<String, CallbackReceiverPrx> entry : registeredClients.entrySet()) {
            String clientHostName = entry.getKey();
            if(clientHostName.contains(hostname))
                key = clientHostName;
                break;
        }
        if(key != null)
            return registeredClients.get(key);
        else
            return null;
    }

    public static synchronized String getAllClients(){
        String output = "";
        for (Map.Entry<String, CallbackReceiverPrx> entry : registeredClients.entrySet()) {
            String clientHostName = entry.getKey();
            output+=clientHostName;
            // Puedes mostrar más detalles del cliente si es necesario
            // Por ejemplo, puedes llamar a métodos en el cliente para obtener información adicional
        }
        return output;
    }


}