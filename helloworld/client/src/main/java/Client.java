import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import Demo.*;
import java.net.InetAddress;

import java.io.*;

public class Client{
	public static void main(String[] args){
		try (Communicator communicator = Util.initialize(args, "client.cfg")){
			
			try{
				PrinterPrx printerPrx = PrinterPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy"));
				
				if (printerPrx == null){

					throw new Error("Invalid proxy");
				}

				com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
				com.zeroc.Ice.Object object = new CallbackReceiverI();
				adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("callbackReceiver"));
				adapter.activate();


				CallbackReceiverPrx receiver =
				CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

					// Obtener el hostname del cliente
					String hostname = InetAddress.getLocalHost().getHostName();
					hostname = whoami() + "@" + hostname;
					printerPrx.registerClient(hostname, receiver);
					
					String userInput = "Default text";

					System.out.print("Enter a message (type 'exit' to quit): ");

					while ((userInput = reader.readLine()) != null) {
						/*
						if (userInput.equalsIgnoreCase("callback")){
							printerPrx.initiateCallback(receiver);
						}*/
						String result = printerPrx.printString(userInput+"-"+hostname);

						System.out.println(result);

						if (userInput.equalsIgnoreCase("exit")){

							break;

						}

						System.out.print("Enter a message (type 'exit' to quit): ");

					}

					printerPrx.unregisterClient(hostname);

				} catch (Exception e) {

					e.printStackTrace();

				}
			} catch (com.zeroc.Ice.ConnectionTimeoutException e){
				System.out.println("-------[WARNIG]------- \n Timeout!");
				e.printStackTrace();
			}
		}
	}

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
