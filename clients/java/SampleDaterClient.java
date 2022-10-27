import java.io.*;
import java.net.*;
import java.util.*;

public class SampleDaterClient {

  public static void main(String[] args) throws Exception {
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    try {
      socket = new Socket("localhost", 20000);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    } 
    catch (UnknownHostException e) {
      System.out.println("Unknown host");
      System.exit(-1);
    } 
    catch (IOException e) {
      System.out.println("No I/O");
      System.exit(-1);
    }  
    out.println("Person");
    String inputLine = in.readLine();
    System.out.println("Server says:" + inputLine);    
    int N = Integer.parseInt(inputLine.split(":")[1]);
    if(N < 2 || N > 200) {
      System.out.println("invalid N");
      System.exit(-1);
    }   
    Thread.sleep(100);
    String weights = generate_init_weight(N);
    out.println(weights);

    for(int p = 0; p < 20; p++) {
      inputLine = in.readLine();
      System.out.println("Server says:" + inputLine);
      String [] candidate_values = inputLine.split(":");
      Thread.sleep(100);    
      weights = generate_adjusted_weight(N, candidate_values);
      out.println(weights);
 
    }  
    out.close();
    in.close();
    socket.close();
  }
  private static String generate_init_weight(int N){
    // TO DO: Change weights to your own algorithm
    String weights ="1:-1";
    for(int i = 2; i < N; i++)
      weights += ":0";
    return weights;
  }

  private static String generate_adjusted_weight(int N, String [] candidate_values){
    // TO DO: Change weights to your own algorithm
    String weights ="1:-1";
    for(int i = 2; i < N; i++)
      weights += ":0";
    return weights;
  }
}