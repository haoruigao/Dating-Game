import java.io.*;
import java.util.*;
import java.net.*;
import java.math.*;

public class DatingGameServer {
  static ServerSocket sSocketDater, sSocketMatchMaker;
  static int datePort = 20000, matchMakerPort = 20001;  

  public static void main(String[] args) {
    if(args.length!=1) {
      System.out.println("Usage: java DatingGameServer numAttributes");   
      System.exit(-1);
    }
    int numAttributes = Integer.parseInt(args[0]);
    try {
      sSocketDater = new ServerSocket(datePort);
      sSocketMatchMaker = new ServerSocket(matchMakerPort);     
      System.out.println("Waiting for clients ...");
    }
    catch(IOException e) {
      System.out.println("Could not listen on port: 20000 or 20001");
      System.exit(-1);
    }
    try {   
      Thread threadDater = new Thread(new DatingGameTask(
          sSocketDater.accept(), sSocketDater, numAttributes));
      threadDater.start();
      Thread threadMatcher = new Thread(new DatingGameTask(
          sSocketMatchMaker.accept(), sSocketMatchMaker, numAttributes));
      threadMatcher.start();        
    } 
    catch (IOException e) {
      System.out.println(
        "IO failure or Person was disconnected or Matchmaker logged in early");
      System.exit(-1);
    } 
  }
} 

class DatingGameTask implements Runnable {
  final static Object lock = new Object();
  static Vector<BigDecimal> daterProfile = new Vector<BigDecimal>();
  static Vector<BigDecimal> initialDaterProfile = new Vector<BigDecimal>();
  static Vector<BigDecimal> prevDaterProfile = new Vector<BigDecimal>();
  static String sharedCandidateWeightString;
  static String earlyExit = "";
  int[] seed = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
  Socket socket = null;
  PrintWriter out = null;
  BufferedReader in = null;
  ServerSocket sSocket = null;
  int N;
  long totTime = 0;

  class TimeoutTask extends TimerTask {
    String myClientType;
    public TimeoutTask(String clientType) {myClientType = clientType;}
    public void run() {
        System.out.println(myClientType + " has run out of time!");
        System.exit(-1);
    }
  }

  public DatingGameTask(Socket socket, ServerSocket sSocket, int numAttributes) {
    N = numAttributes;
    this.socket = socket;
    this.sSocket = sSocket;
    try {
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    catch (IOException e) {
      System.err.println("No I/O" + e);
      System.exit(-1);
    }
  }
  public void run(){
    try {
      processRequest();
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }
  public void processRequest() {
    String clientType = "";
    try {
      clientType = in.readLine();
      if(clientType.equals("Person")) 
        daterLogic();
      if(clientType.equals("Matchmaker"))
        matchMakerLogic();
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }
    finally {
      try {
        out.println("END");
        System.out.println("Server disconnecting " + clientType + "... ");
        out.close();
        in.close();
        socket.close();        
        sSocket.close();        
      }
      catch(IOException e) {
        e.printStackTrace(System.out);
        System.exit(-1);
      }
    }
  }
  public void endGame(String exitString){
    System.out.println(exitString);
    out.println(exitString);
    earlyExit = exitString;
    synchronized (lock) {
      lock.notify();
    }
  }
  public String getClientInput(String clientType) throws IOException {
    Timer timer = new Timer();
    timer.schedule(new TimeoutTask(clientType), 60*1000*2 - totTime);
    long currentTime = System.currentTimeMillis();
    String inputLine = in.readLine();
    timer.cancel();
    totTime += System.currentTimeMillis() - currentTime;
    System.out.println(clientType + " sent: " + inputLine + "\n" + clientType +
      " has used " + (totTime/1000) + " seconds of their 120 available seconds");
    return inputLine;
  }
  public void daterLogic() throws IOException {
    System.out.println("Interacting with the Dater.");
    // Player can change up to 5% of attributes by up to 20% of original value
    BigDecimal maxNumChanges = BigDecimal.valueOf(0.05).multiply(BigDecimal.valueOf(N));
    BigDecimal maxAlterationPct = BigDecimal.valueOf(0.2);
    int turn = 0;
    out.println("N:" + N);
    while (turn < 21) {
      int changes = 0;
      String[] daterInput = getClientInput("Dater").split(":");
      if(daterInput.length != N) {
        endGame(("Dater profile did not specify exactly " + N + " attributes"));
        return;
      }
      BigDecimal abs_sum_weights = BigDecimal.ZERO, sum_weights = BigDecimal.ZERO;
      for(int attribute_index = 0; attribute_index < N; attribute_index++) {
        BigDecimal attribute_weight = new BigDecimal(daterInput[attribute_index]);
        if(attribute_weight.scale() > 2) {
          endGame("Dater expressed too much precision on an attribute weight");
          return;          
        }
        daterProfile.add(attribute_index, attribute_weight);
        BigDecimal check;
        if(turn == 0){
          initialDaterProfile.add(attribute_weight);
          check = attribute_weight;
        }
        else{
          check = prevDaterProfile.get(attribute_index);
        }
        BigDecimal orig_attribute_weight = check;
        BigDecimal weight_change = orig_attribute_weight.subtract(attribute_weight);
        if(weight_change.compareTo(BigDecimal.ZERO) != 0) {
          if(orig_attribute_weight.compareTo(BigDecimal.ZERO) == 0
            || weight_change.divide(orig_attribute_weight, RoundingMode.HALF_EVEN)
              .abs().compareTo(maxAlterationPct) > 0) {
            endGame("Dater changed an initial attribute by more than 20%");
            return;
          }          
          changes++;
          if(BigDecimal.valueOf(changes).compareTo(maxNumChanges) > 0) {
            endGame("Dater changed more than 5% of the attributes");
            return;               
          }          
        }
        sum_weights = sum_weights.add(attribute_weight);
        abs_sum_weights = abs_sum_weights.add(attribute_weight.abs());
        if (prevDaterProfile.size() == N){
          prevDaterProfile.remove(attribute_index);
        }
        prevDaterProfile.add(attribute_index, attribute_weight);
      }

      if(sum_weights.compareTo(BigDecimal.ZERO) != 0 || 
        abs_sum_weights.compareTo(BigDecimal.valueOf(2)) != 0) {
        endGame("Dater attribute weights don't meet requirments");
        return;
      }
      if (turn == 0)
        System.out.println("Initial dater profile accepted");
      else
        System.out.println("Round " + turn + " dater profile accepted");        
      turn++;      
      synchronized (lock) {
        // Notify Matchmaker a profile is set to base scoring on
        lock.notify();
        try {
           // Wait for a new C if one should be coming and then send it to P
          if (turn < 21) {
            lock.wait();
            if (earlyExit != "") {
              out.println(earlyExit);
              return;
            }
            out.println(sharedCandidateWeightString);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.out.println(e.getLocalizedMessage());
        }
      }
    }
  }
  public void matchMakerLogic() throws IOException, InterruptedException {
    if(daterProfile.size() == N) { // Ensure dater has set up a profile
      System.out.println("Sleeping for 5 seconds then interacting with the Matchmaker.");
      Thread.sleep(5000); // Sleep 5 seconds so we can switch to a display of the fun.   
      for(int i = 0; i < 20; i++) { // Generate 20 random candidates
        String candidateString = "";
        Random generator = new Random(seed[i]);
        BigDecimal score = BigDecimal.ZERO;
        for(int k = 0; k < N; k++) {
          if(k > 0)
            candidateString+=":";
          int weight = generator.nextInt(2);
          candidateString += Integer.toString(weight);
          score = score.add(daterProfile.get(k).multiply(BigDecimal.valueOf(weight)));
        }
        System.out.println(candidateString);
        out.println((candidateString+":"+score));
      }
      int bestCandidate = 1;
      BigDecimal bestScore = BigDecimal.valueOf(-1);
      // Read 20 candidates from matchmaker but stop if `ideal` candidate is found.
      for (int candidateNumber = 1; candidateNumber <= 20; candidateNumber++) {
        sharedCandidateWeightString = getClientInput("Matchmaker");
        String[] candidateWeights = sharedCandidateWeightString.split(":");       
        if(candidateWeights.length == N) {
          BigDecimal attribute_weights[] = new BigDecimal[N];
          for(int i = 0; i < N; i++) {
            try {
              attribute_weights[i] = new BigDecimal(candidateWeights[i]);
            }
            catch (NumberFormatException e) {
              endGame(("Matchmaker candidate " + candidateNumber +
                " is not parseable as a floating point number."));
              return;              
            }
            if ((attribute_weights[i].scale() > 4) ||
                (attribute_weights[i].compareTo(BigDecimal.ONE) > 0) ||
                (attribute_weights[i].compareTo(BigDecimal.ZERO) < 0)) {
              endGame(("Matchmaker candidate " + candidateNumber +
                " has invalid attribute weights."));
              return;
            }
          }
          synchronized (lock) {
            // Notify Dater to reset dating profile
            lock.notify();
            try {
              lock.wait(); // Get new profile before scoring C
              if (earlyExit != "") {
                out.println(earlyExit);
                return;
              }
            } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  System.out.println(e.getLocalizedMessage());
            }
          }
          BigDecimal candidateScore = BigDecimal.ZERO;
          for(int i = 0; i < N; i++)
            candidateScore = candidateScore.add(attribute_weights[i].multiply(
              daterProfile.get(i))).setScale(4, RoundingMode.HALF_EVEN);
          if((candidateScore.compareTo(BigDecimal.ONE) == 0)) {
            endGame("Matchmaker Value: `1.0000, " + candidateNumber + "`"); 
            return;
          }
          if(candidateScore.compareTo(bestScore) > 0) {
            bestScore = candidateScore;
            bestCandidate = candidateNumber;
          }
          out.println("SCORE:" + candidateScore);
          System.out.println("SCORE: " +  candidateScore);
        }
        else {
          endGame(("Matchmaker candidate does not contain " + N + " attributes"));
          return;
        }
      }
      endGame("Matchmaker Value: `" +  bestScore + ", " + bestCandidate + "`");
    }
    else 
      endGame("Matchmaker logged in early");
  }
}