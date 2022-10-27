import socket
import time
import sys
from signal import signal, SIGPIPE, SIG_DFL  
signal(SIGPIPE,SIG_DFL)

clientsocket = socket.socket()


def generate_init_weight(N):
  '''
  TO DO: Change weights to your own algorithm
  DON'T FORGET THE \n
  '''

  weights = "1:-1" + ":0"*(N-2) + "\n"

  return weights


def generate_adjusted_weight(N, candidate_values):
  '''
  N: int
  candidates_values: string array
  '''
  
  '''
  TO DO: Change weights to your own algorithm
  DON'T FORGET THE \n
  '''
  
  weights = "1:-1" + ":0"*(N-2) + "\n"

  return weights
  

if __name__ == '__main__':
  clientsocket.connect(('localhost', 20000))
  socketfile = clientsocket.makefile('rw')
  socketfile.write('Person\n')
  socketfile.flush()
  inputLine = socketfile.readline()
  print("Server says:" + inputLine)
  N = (int) ((inputLine.split(":"))[1])
  if (N < 2 or N > 200):
    print("invalid N")
    sys.exit()
    
  # Generate the initial weights (First Round)
  time.sleep(.1)
  weights_init = generate_init_weight(N)
  socketfile.write(weights_init)
  socketfile.flush()
  
  # Generate the adjusted weights (Next Rounds)
  for i in range(20):
    inputLine = socketfile.readline()
    if inputLine:
      print("Server says:" + inputLine)
    candidate_values = inputLine.split(":")
    time.sleep(.1)
    weights = generate_adjusted_weight(N, candidate_values)
    socketfile.write(weights)
    socketfile.flush()
    
  clientsocket.close()
