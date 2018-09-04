# Publisher Reviewer Decision Tree

Author: Yuanxu Wu  

# How to run this program

## Prerequisites  
1. Java 1.8.0  

## How to compile and run the three programs:  
1. javac Main.java  
2. java Main <input file name> e.g. java Main input.txt  
3. then follow the instruction type in "yes" or "no" to indicate the review of reviewer.  

## Input txt file format:  
2 50000 -2000 0.2  
400 0.9 0.2  
100 0.6 0.3  

## Example:  
$ javac Main.java  
$ java Main input.txt  
Utility of Success: 50000.00, Utility of Failure: -2000.00, P(S)=0.20  
Reviewer 1: Utility=400 P(R=T|S)=0.90 P(R=T|F)=0.20  
Reviewer 2: Utility=100 P(R=T|S)=0.60 P(R=T|F)=0.30  

Expected value: 8540.0  
Consult Reviewer 2: no  
Consult Reviewer 1: yes  
Publish  
