import socket
import random
import time
import sys
from signal import signal, SIGPIPE, SIG_DFL  
signal(SIGPIPE,SIG_DFL)

clientsocket = socket.socket()

def generate_candidates(i, N, score):
  
  '''
  TO DO: Change candidates to your own algorithm
  DON'T FORGET THE \n
  '''
  if i < 30:
    candidates = "0." + ":0.".join(str(e)
                                   for e in [str(int(random.random()*10000))
                                             for i in range(N)])+"\n"
  else:                                       
    candidates = "1" + ":0"*(N-1) + "\n"
  return candidates

if __name__ == '__main__':
  clientsocket.connect(('localhost', 20001))
  socketfile = clientsocket.makefile('rw')
  socketfile.write('Matchmaker\n')
  socketfile.flush()
  # Init
  N = 0
    
  # Start Read from the 20 Random Candidates and scores from Server (First Round)
  for i in range(20):
    inputLine = socketfile.readline()
    print("Server says:" + inputLine)
    tmp = inputLine.split(":")
    # Check N and set
    if i == 0:
      # Set
      N = len(tmp) - 1
      if (N < 2 or N > 200):
        print("invalid N")
        sys.exit()
    candidates = tmp[:-1]
    score = tmp[-1]
    '''
    TO DO: You could use the candidates and score to implement algorithm
    '''

  # Start Write Candidates to P and Read from the Scores (Next Rounds)
  for i in range(20, 40):
    candidates = generate_candidates(i, N, score)
    socketfile.write(candidates)
    socketfile.flush() 
    inputline = socketfile.readline()
    print("Server says:" + inputline)
    if inputline == "END\n":
      sys.exit()
    score = inputline.split(":")[1]
    time.sleep(.2)
 
  print("Server says:" + socketfile.readline())
  clientsocket.close()
