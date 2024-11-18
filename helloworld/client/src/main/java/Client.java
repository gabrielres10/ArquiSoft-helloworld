import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import Demo.*;
import java.net.InetAddress;
import java.io.*;


/**
 * This is the main class that is compiled by "gradle build" command
 * This class contains the main conection to the server and the main method to run the client.
 * 
 * Read the documentation cautiously to understand the code.
 */
public class Client{
	public static void main(String[] args){
		// This try block is used to initialize the connection with the server by using the information in the client.cfg file.
		try (Communicator communicator = Util.initialize(args, "client.cfg")){
			
			try{
				// This line is used to get the proxy of the server. The server proxy must be activated first in server side
				// Note that "ChatServerPrx" class is not declared anywhere, because it is inferred
				// by the Ice Middleware over the EncryptedChat.ice file.
				ChatServerPrx chatServerPrx = ChatServerPrx.checkedCast(communicator.propertyToProxy("ChatServer.Proxy"));
				
				if (chatServerPrx == null){ // If the conection fails. This error might be thrown because of the port or the IP of the server.
					throw new Error("Invalid proxy");
				}

				//This is the adapter for the chat client this name can be anything as long as it matches with the name used in the client.cfg file
				com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Chat.Client"); 

				//This is the object that will be used to create the proxy for the client. ChatClientI is the class that implements the ChatClient interface.
				//There is no need to import the ChatClientI class because it is in the same package as the Client class.
				com.zeroc.Ice.Object object = new ChatClientI();

				//This line is used to add the object to the adapter, wich is the object that will be used to create the proxy for the client.
				adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("chatClient"));
				adapter.activate();

				//This line is used to create the proxy for the client. The proxy must be created and activated for it to be able to
				//subscribe to the list of clients in the server side.
				ChatClientPrx client =
				ChatClientPrx.uncheckedCast(adapter.createProxy(
                com.zeroc.Ice.Util.stringToIdentity("chatClient")));


				//This try block is used to get the input from the user and send it to the server.
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

					// This line is used to get the hostname of the client. The hostname is used to identify the client in the server side.
					String hostname = InetAddress.getLocalHost().getHostName();
					hostname = "user" + "@" + hostname;

					// This line is used to register the client in the server side. The client must be registered in the server side
					chatServerPrx.registerClient(hostname, client); // This is the way the server admits any amount of clients.
					
					String userInput = "Default text";
					System.out.print("Enter a message (type 'exit' to quit): ");

					while ((userInput = reader.readLine()) != null) {
						String result = chatServerPrx.sendMessage(userInput+"-"+hostname);

						System.out.println(result);

						if (userInput.equalsIgnoreCase("exit")){
							break;
						}

						System.out.print("Enter a message (type 'exit' to quit): ");

					}

					// This line is used to unregister the client in the server side. The client must be unregistered in the server side
					chatServerPrx.unregisterClient(hostname);

				} catch (Exception e) {

					e.printStackTrace();

				}
			} catch (com.zeroc.Ice.ConnectionTimeoutException e){
				System.out.println("-------[WARNIG]------- \n Timeout!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method is used to get the username of the client. This method is used to get the username of the client
	 */
	private static String whoami() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("whoami");
			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			return line;

		} catch (IOException  e) {
				e.printStackTrace();
		}

		return "";
  }

}
