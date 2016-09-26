# Deterministic Finite Automata Minimizer

### Author: Abhay Mittal

This program implements a minimization algorithm for deterministic finite automata to reduce it to the minimum number of states. The algorithm is from the book __"Introduction to Automata Theory, Languages, and Computation"__ (2nd edition) by __John E. Hopcroft, Rajeev Motwani__ and __Jeffrey D. Ullman__.

It is possible to see the intermediate steps in the program.

###Input###
A DFA in its 5 tuple form as:
	Q  : The set of states
	S   : The alphabet set
	q0 : Initial state
	F : The final states of the automaton 
	T:  Transition table 

#### Example Input####

```
Q
A B C
-----------------------
S
0 1
------------------------
qo 
A
------------------------
F
A C
-----------------------
T 0 1
A B C
B C A
C A B
```

###Output###
The output file is renamed as min<inputfile>. Thus if the input file was named  abc, the output file will be minabc.

The output file contains the minimized dfa in the same format as provided in the input file. 
Note: Similar states in the output are combined with a "," in between.

####Example Output (For example input above)####
```
--------------------------------------------------------------------------------
Q
A,E   B,H   C   D,F   G   
-------------------------------------------------------------------------------

S
0   1   
-------------------------------------------------------------------------------

qo
A,E

-------------------------------------------------------------------------------

F
C   
-------------------------------------------------------------------------------

T   0   1   
A,E B,H D,F 
B,H G   C   
C   A,E C   
D,F C   G   
G   G   A,E 
```
	