# Dating-Game

This is the implementation of the architecture for the [dating game](https://cs.nyu.edu/courses/fall22/CSCI-GA.2965-001/dating.html). Any questions, requests, or bug reporting, please contact us at Haorui Gao (hg2524@nyu.edu).

## Description

An in-depth paragraph about your project and overview of use.

## Getting Started

### Dependencies

* Java 7 SDK or newer

### Compiling and Running

* Compile the game server with a java 7 or newer compiler.
```
javac DatingGameServer.java
```
* Command to run a server to host an instance of the dating game with N candidate attributes.
```
java DatingGameServer {N}
```
### Date P

The Dater will be communicating with the game server over a socket at **port `20000`**.  

### Matchmaker M

The Matchmaker will be communicating with the game server over a socket at **port `20001`**.  

## Details
### Dater P
Dater will send the message of its modified weights to each attributes, the value is [-1, 1] inclusive, and should be decimal values having at most two digits to the right of the decimal point e.g. 0.13 but not 0.134. The sum of positive weights should be 1, and the sum of negative weights should be -1.  
  
The format P sent should be (delimited with comma)

```
w1,w2,...,wn
```


e.g. N = 5
```
0.25,0.10,0.65,-0.40,-0.60
```

## Authors

Contributors names and contact info

ex. Dominique Pizzie  
ex. [@DomPizzie](https://twitter.com/dompizzie)
