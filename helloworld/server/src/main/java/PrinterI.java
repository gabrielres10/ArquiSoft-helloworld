import Demo.*;
import com.zeroc.Ice.Current;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PrinterI implements Printer {

  private ExecutorService threadPool = Executors.newCachedThreadPool();
  private List<Long> latencies = new ArrayList<>();

  @Override
  public void unregisterClient(String hostName, com.zeroc.Ice.Current current) {
    //new client connected
    Server.unregisterClient(hostName);
  }

  @Override
  public void registerClient(
    String hostName,
    CallbackReceiverPrx callback,
    com.zeroc.Ice.Current current
  ) {
    //new client connected
    Server.registerClient(hostName, callback);
  }

  @Override
  public void initiateCallback(
    CallbackReceiverPrx proxy,
    String msg,
    com.zeroc.Ice.Current current
  ) {
    try {
      proxy.callback(msg);
    } catch (com.zeroc.Ice.LocalException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void shutdown(com.zeroc.Ice.Current current) {
    System.out.println("Shutting down...");
    try {
      current.adapter.getCommunicator().shutdown();
    } catch (com.zeroc.Ice.LocalException ex) {
      ex.printStackTrace();
    }
  }

  public String printString(String msg, com.zeroc.Ice.Current current) {
    String clientHN = msg.split("-")[1];
    msg = msg.split("-")[0];

    final String finalMsg = msg;
    final String finalClientHN = clientHN;

    System.out.println("\n [MENSAJE] - " + clientHN + ": " + msg + "\n");
    long startTime = System.currentTimeMillis();
    Server.totalRequestsReceived++;

    Future<String> resultFuture = threadPool.submit(() ->
      manageRequest(finalMsg, finalClientHN, current)
    );
    try {
      String result = resultFuture.get(); // Espera hasta que la solicitud se haya procesado y obtiene el resultado
      Server.successfulRequestsProcessed++;
      long endTime = System.currentTimeMillis();
      long requestTime = endTime - startTime; // Tiempo que dura la operación
      latencies.add(requestTime); // add new latency
      Server.totalTime += requestTime;
      Server.requestCount++;
      System.out.println("Throughput (solicitudes por segundo): " + calculateThroughput()); //print the throughput
      System.out.println("Tiempo de respuesta promedio (ms): " + (Server.totalTime / Server.requestCount)); //print the response time
      System.out.println(calculateDeadline(requestTime));
      System.out.println("Jitter de respuesta: " + calculateJitter(latencies) + " ms"); //print jitter of response
      System.out.println("Tasa de pérdida (eventos perdidos por segundo): " + calculateLossRate());

      return result;
    } catch (Exception exception) {
      Server.unprocessedRequests++;
      System.out.println("Evento no procesado. Tasa total de no procesamiento (por segundo): " + calculateUnprocessRate());
      exception.printStackTrace();
      return "Error al procesar la solicitud";
    }
  }

  private String manageRequest(
    String msg,
    String hostName,
    com.zeroc.Ice.Current current
  ) {
    String command = "";
    if (msg.startsWith("!")) {
      command = msg.split("!")[1];
      return executeCommand(command); //return !custom command
    } else {
      try {
        int num = Integer.parseInt(msg);
        if (num > 0) {
          return printPrimes(num); //return prime number
        } else {
          return (
            msg +
            " no es un número positivo! Prueba otros comandos que sean válidos \n"
          );
        }
      } catch (NumberFormatException e) {
        switch (msg.split(" ")[0].toLowerCase()) {
          case "listifs":
            command = "ifconfig";
            return executeCommand(command);
          case "listports":
            command = "nmap " + msg.split(" ")[1];
            return executeCommand(command);
          case "exit":
            return ("Bye bye, " + hostName + "!");
          case "list":
            if (
              msg.split(" ")[1].equalsIgnoreCase("clients")
            ) return Server.getAllClients();
          case "to":
            CallbackReceiverPrx destination = null;
            if (msg.split(" ").length > 1) {
              destination =
                Server.getClient(msg.split(" ")[1].replace(":", ""));
              //System.out.println(destination); //This section will allow to detect if a destination is being missprocessed
              if (destination != null && msg.split(":").length == 2) {
                String message = hostName + ": " + msg.split(":")[1];
                initiateCallback(destination, message, current);
              }
            }
            return (msg.split(":").length != 2)
              ? "Tu mensaje debe ser similar a \"to X:\" donde el mensaje irá después de :"
              : (destination == null)
                ? "El cliente " + msg.split(" ")[1] + " no existe"
                : "";
          default:
            if (msg.startsWith("BC")) {
              if (msg.split(":").length > 1) {
                String message = msg.split(":")[1];
                for (CallbackReceiverPrx callback : Server.getCallbacks()) {
                  initiateCallback(callback, message, current);
                }
              }
              return (
                  msg.split(":").length < 1 ||
                  (msg.split(":").length < 1 && msg.split(" ").length > 1)
                )
                ? "Debes ingresar un mensaje después de BC: (recuerda separar el mensaje con \":\")"
                : "";
            }
            return ("Por favor ingresa un comando válido");
        }
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

      output =
        output + "El comando ha terminado con código de salida: " + exitCode;
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

  private long calculateJitter(List<Long> latencies) {
    if (latencies.size() < 2) {
      return 0; // No hay suficientes latencias para calcular el jitter
    }

    // Calcular la desviación estándar de las latencias
    double sumSquaredDifferences = 0;
    long meanLatency = calculateMean(latencies);

    for (long latency : latencies) {
      long difference = latency - meanLatency;
      sumSquaredDifferences += Math.pow(difference, 2);
    }

    double variance = sumSquaredDifferences / (latencies.size() - 1);
    double jitter = Math.sqrt(variance);

    return Math.round(jitter);
  }

  private long calculateMean(List<Long> latencies) {
    long sum = 0;

    for (long latency : latencies) {
      sum += latency;
    }

    return sum / latencies.size();
  }

  private double calculateThroughput(){
    return (double) Server.requestCount / ((double) Server.totalTime / 1000);
  }

  private double calculateUnprocessRate(){
    return (double) Server.unprocessedRequests / ((double) Server.totalTime / 1000);
  }

  private String calculateDeadline(long requestTime){
    String output = "";
    long deadline = 5000; // stablish 5 seconds as limit time to respond
    if (requestTime <= deadline) {
      output = "Última solicitud procesada dentro del plazo de 5s: " + requestTime + "/" + deadline + " (ms)";
    } else {
      output = "La última solicitud excedió el plazo de 5s: " + requestTime + "/" + deadline + " (ms)";
    }
    return output;
  }

  private double calculateLossRate() {
    if (Server.totalRequestsReceived == 0) {
        return 0; // No se han recibido solicitudes, tasa de pérdida cero
    }

    return (double) (Server.totalRequestsReceived - Server.successfulRequestsProcessed) / ((double) Server.totalTime / 1000);
  }
  
}
