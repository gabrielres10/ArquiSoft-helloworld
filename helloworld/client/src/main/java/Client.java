import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import Demo.PrinterPrx;
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
			

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
					printerPrx.newClient();

					String userInput = "Default text";

					System.out.print("Enter a message (type 'exit' to quit): ");

					while ((userInput = reader.readLine()) != null) {

						// Obtener el hostname del cliente
							String hostname = InetAddress.getLocalHost().getHostName();
							
						String result = printerPrx.printString(userInput+"-"+whoami() + "@" + hostname);

						System.out.println(result);

						if (userInput.equalsIgnoreCase("exit")){

							break;

						}

						System.out.print("Enter a message (type 'exit' to quit): ");

					}

					printerPrx.disconnectClient();

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
