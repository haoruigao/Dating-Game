# Dating-Game

This is the implementation of the architecture for the [dating game](https://cs.nyu.edu/courses/fall22/CSCI-GA.2965-001/dating.html). Any questions, requests, or bug reporting, please contact us at Haorui Gao (hg2524@nyu.edu).

## Description

Matchmaker M is trying to find the `ideal` candidate C for player P.

P indicates initial preferences for N attributes of C but tries to be `hard to please` by seeing M's Cs and modifying their initial preference within a set of constraints after each proposal from M.

The game involves M specifying a series of candidate Cs for P.  The Server will report to M how much P likes those dates (score between -1 and 1, where 1 is good and -1 is very bad).  P's criteria for liking a date or not depends on the weights P gives to various attributes -- e.g. literary knowledge, ability to solve puzzles, and others.  The weights may be positive or negative ranging from -1 to 1 and specified as decimal values having at most two digits to the right of the decimal point, e.g. 0.13 but not 0.134.

P must respond with updated preferences after each C is proposed by M.  In each response, P may modify 5% of their attribute weights (P may choose which ones) by 20% each with respect to the original weights.  For example, if a chosen attribute has a weight of 0.4 in P's original criteria, then P can modify it to any value between 0.4 - (0.2x0.4) and 0.4 + (0.2x0.4).  P does these modifications after seeing M's candidates.  (Modifications are always with respect to P's original choice of weights.)  Also, in both the original setting of weights and after all modifications, P must ensure that the sum of P's positive weights (there must be at least one such weight) must be 1 and the sum of the negative weights (there must be at least one such weight) must be -1.

A candidate C has values for each of N (bigger than 1 and not more than 200) attributes -- each value lies between 0 and 1 inclusive and must have four or fewer digits of precision (without this constraint, there is a way to discover the sign of the weight that P ascribes to each attribute).  Given the weights P has assigned to each attribute w1, ..., wn and the values C has for each attribute v1, ..., vn, the score P gives to C is simply the dot product of the two vectors.  No candidate should get a score below -1 or above 1, but the ideal candidate will consist solely of 1s and 0s.

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
### Dater P

The Dater will be communicating with the game server over a socket at **port `20000`**.  

### Matchmaker M

The Matchmaker will be communicating with the game server over a socket at **port `20001`**.  

## Details
### Dater P
Dater will send the message of its modified weights after first round to each attributes, the value lies [-1, 1] inclusive, and should be decimal values having at most two digits to the right of the decimal point e.g. 0.13 but not 0.134.  
* The sum of positive weights should be 1, and the sum of negative weights should be -1.
* In each response, P may modify 5% of their attribute weights (P may choose which ones) by 20% each with respect to the original weights.

The format P `received` should be (delimited with colon) - Candidates values
```
v1:v2:...:vn
```

The format P `sent` should be (delimited with colon) - Weights

```
w1:w2:...:wn
```


e.g. N = 5
```
0.25:0.10:0.65:-0.40:-0.60
```

### Matchmaker M
Matchmaker will send the message of its manipulated candidates with each value lies [0, 1] inclusive and must have four or fewer digits of precision.

First round, received 20 random candidates from server.   

The format M `received` should be - Random Candidates values && Score
```
v1:v2:...:vn
SCORE:{S}
```

Second round, manufacture up to 20 additional candidates.  

The format M `sent` should be - Candidates values
```
v1:v2:...:vn
```

e.g. N = 5
```
0.9473:0.9636:0.4428:0.8956:0.5137
```

The format M `received` should be
```
SCORE:{S}
```


## Playline

The game consists of up to 20 rounds where
INIT
* P initializes the weights
* Server initializes 20 random candidates
* M receives the 20 candidates and respective scores
****
LOOP
* M sends a candidate C to S
  * Valid example message for C: `0.9473:0.9636:0.4428:0.8956:0.5137`
* S verifies C meets the constraints
  * Otherwise M loses
* S sends C to P
  * Valid example message for C: `0.9473:0.9636:0.4428:0.8956:0.5137`
* P sends a modified dating profile to S
  * Valid example message for profile: `0.27:0.08:0.65:-0.40:-0.60`
* S verifies the modified profile meets the constraints
  * Otherwise P loses
* S checks if the score using the original profile's weights is 1
  * If so, the game ends at this round
* S sends M a message of the form `SCORE:{S}` where S is the score from the modified dating profile with exactly four digits of precision.

## Implementation

Sample clients are available in java, python, and c++, you could take them as reference and implement yours in `DateClient` and `MatchMakerClient`.

## Submission

Each group please submit your code with two files and README using a zip, and send to hg2524@nyu.edu by Wed 11:59 pm. Thanks!

## Authors

* Haorui Gao
* Youqing Liang
