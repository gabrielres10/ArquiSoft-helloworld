module Demo {

    interface CallbackReceiver
    {
        void callback(string msg);
    }
    interface Printer {
        string printString(string msg);
        void registerClient(string hostname, CallbackReceiver* proxy);
        void unregisterClient(string hostname);
        void initiateCallback(CallbackReceiver* proxy, string msg);
        void shutdown();
    }
    interface ChatClient {
        void receiveMessage(string msg);
    }

    interface ChatServer {
        string sendMessage(string msg);
        void registerClient(string hostname, ChatClient* proxy);
        void unregisterClient(string hostname);
        void shutdown();
    }
}
