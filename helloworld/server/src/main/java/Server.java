public class Server {
    private static int clientCount = 0;

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
    public static synchronized void incrementClientCount() {
        this.clientCount++;
        System.out.println("Client connected. Total: " + clientCount);
    }

    // Método para obtener el número actual de clientes conectados
    public static synchronized int getClientCount() {
        return clientCount;
    }
}