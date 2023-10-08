import com.zeroc.Ice.Current;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PrinterI implements Demo.Printer {

  public String printString(String msg, com.zeroc.Ice.Current current) {
    String clientHN = msg.split("-")[1];
    msg = msg.split("-")[0];

    System.out.println("[MENSAJE] - " + clientHN + ": " + msg + "\n");

    try {
      int num = Integer.parseInt(msg);
      if (num > 0) {
        System.out.println("Consulta hecha por " + clientHN + ":\n");
        return printPrimes(num);
      } else {
        return msg + " no es un número positivo! Prueba otros comandos que sean válidos \n";
      }
    } catch (NumberFormatException e) {
      return manageRequest(msg, clientHN);
    }
  }

  private String manageRequest(String msg, String hostName) {
    String command = "";
    if (msg.startsWith("!")) {
      command = msg.split("!")[1];
      System.out.println("Consulta hecha por " + hostName + ":\n");
      return executeCommand(command);
    } else {
      switch (msg.split(" ")[0].toLowerCase()) {
        case "listifs":
          System.out.println("Consulta hecha por " + hostName + ":\n");
          command = "ifconfig";
          return executeCommand(command);
          
        case "listports":
          System.out.println("Consulta hecha por " + hostName + ":\n");
          command = "nmap " + msg.split(" ")[1];
          return executeCommand(command);
          
        case "exit":
          return ("Bye bye, " + hostName + "!");
          
        default:
          return ("Por favor ingresa un comando válido");
          
      }
    }
  }

  private String executeCommand(String command) {
    String output = "";
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream())
      );
      String line;
      while ((line = reader.readLine()) != null) {
        output = output + line + "\n";
      }

      int exitCode = process.waitFor();
      
      output = output + "El comando ha terminado con código de salida: " + exitCode;
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return "No fue posible ejecutar el comando";
    }
    return output;
  }

  public static List<Integer> encontrarFactoresPrimos(int n) {
    List<Integer> factoresPrimos = new ArrayList<>();

    for (int i = 2; i <= n; i++) {
      while (n % i == 0) {
        factoresPrimos.add(i);
        n /= i;
      }
    }

    return factoresPrimos;
  }

  public String printPrimes(int num) {
    String output = "Factores primos de " + num + ": \n";
    List<Integer> factoresPrimos = encontrarFactoresPrimos(num);
    for (int factor : factoresPrimos) {
      output = output + factor + "\n";
    }

    return output;
  }
}
