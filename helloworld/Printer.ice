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
    
}
