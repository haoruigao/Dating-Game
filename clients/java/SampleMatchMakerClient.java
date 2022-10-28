import java.io.*;
import java.net.*;
import java.util.*;

public class SampleMatchMakerClient {

  //  TO DO: Change candidates to your own algorithm
  private static String generate_candidates(int i, int N, String score){
    Random generator = new Random();
    String candidate = "";
    if (i < 30){
      for(int k = 0; k < N; k++) {
        if (k>0)
          candidate = candidate + ":";
          candidate = candidate + "0." + (Integer.toString((int)(generator.nextDouble() * 10000)));
      }
    }
    else {
      candidate ="1";
      for(int k = 0; k < N-1; k++)
        candidate += ":0";
    }
    return candidate;
  }

  public static void main(String[] args) throws Exception {
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    try {
      socket = new Socket("localhost", 20001);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } 
    catch (UnknownHostException e) {
      System.out.println("Unknown host");
      System.exit(-1);
    } 
    catch (IOException e) {
      System.out.println("No I/O");
      System.exit(-1);
    }
    out.println("Matchmaker");    
    // INIT
    int N = 0;
    String score = "";
    // Start Read from the 20 Random Candidates and scores from Server (First Round)
    for (int i = 0; i < 20; i++) {
      String inputLine = in.readLine();
      System.out.println("Server says: " + inputLine);
      String [] tmp = inputLine.split(":");
      // Check N and set
      if (i == 0){
        N = tmp.length - 1;
        if(N < 2 || N > 200) {
          System.out.println("invalid N");
          System.exit(-1);
        }
      }
      String [] candidates = Arrays.copyOf(tmp, tmp.length-1);
      score = tmp[tmp.length-1];
      // TO DO: You could use the candidates and score to implement algorithm

    }
    for (int i = 20; i < 40; i++) {
      String candidates = generate_candidates(i, N, score);
      out.println(candidates);
      String inputLine = in.readLine();
      System.out.println("Server says: " + inputLine);
      if (inputLine.equals("END")){
       System.exit(-1);
      }
      String [] tmp = inputLine.split(":");
      score = tmp[1];
      Thread.sleep(200);
    }

    String inputLine = in.readLine();
    System.out.println("Server says: " + inputLine);    
    out.close();
    in.close();
    socket.close();
  }
}