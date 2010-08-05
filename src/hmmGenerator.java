import java.io.*;
import java.util.*;

public class hmmGenerator extends errorModel {

    /** 
     * This class generates a hidden markov model for the given probability matrix, and FER 
     * value. It is initialized and then the generate is called many repeatedly by the RLE. 
     * If the generate returns a 0, then the packet is sent through without a problem,
     * else it is messed up and placed on the receiver buffer
     */

    double prob0, prob1;
    double[][] stateTrans;
    int numBlocks;
    Random stateRandom, lossyRandom;
    int[][] nextState;
    // ***** change - there was nothing here before ******************
    int state;
    int lossyState;
    // *****************************
    
    public hmmGenerator(double lossyProb, double[][] state_trans) {
	
	state = 0;
	lossyState = 0;
	
	
	prob0 = lossyProb;
	prob1 = 1 - lossyProb;
	stateTrans = state_trans;
	//numBlocks = total;
	stateRandom = new Random();
	lossyRandom = new Random();
	nextState = new int[16][2];
	nextState[0][0] = 0;	nextState[0][1] = 1;	nextState[1][0] = 2;	nextState[1][1] = 3;	nextState[2][0] = 4;	nextState[2][1] = 5;
	nextState[3][0] = 6;	nextState[3][1] = 7;	nextState[4][0] = 8;	nextState[4][1] = 9;	nextState[5][0] = 10;	nextState[5][1] = 11;	
	nextState[6][0] = 12;	nextState[6][1] = 13;	nextState[7][0] = 14;	nextState[7][1] = 15;	nextState[8][0] = 0;	nextState[8][1] = 1;
	nextState[9][0] = 2;	nextState[9][1] = 3;	nextState[10][0] = 4;	nextState[10][1] = 5;	nextState[11][0] = 6;   nextState[11][1] = 7;
	nextState[12][0] = 8;   nextState[12][1] = 9;	nextState[13][0] = 10;	nextState[13][1] = 11;	nextState[14][0] = 12;	nextState[14][1] = 13;
	nextState[15][0] = 14;	nextState[15][1] = 15;
    }

    /** 
     * The error model generator - basically the logic is - if the frame returns as 0
     * then we let the packet through fine, otherwise we mess it up in the RLE
     */
    public int generate(){
	int count, currentState, currentLossyState, stateValue, frame;
	//System.out.println("state in hmm generator = " + state + " lossyState is = " + lossyState);
	/** ************ change this - this was non commented before ************
	int state, lossyState;
	count = 0;
	state = 0;
	lossyState = 1;
	*/
	/**************** change this - this was non commented before ************
	while (count < numBlocks) {
	*/
	currentState = state;
	currentLossyState = lossyState;
	stateValue = getNextState(currentState);
	
	if (stateValue == 0) {
	    frame = 0;
	}
	else {
	    frame = getLossyFrame(prob0,prob1);
	    lossyState = nextState[currentLossyState][frame];
	}
	
	// *********** Change needed *************
	//System.out.println(frame);
	// **************************************
	
	state = nextState[currentState][stateValue];

	
	return frame;

	/********************* This was non commented before too **************************
        } 
	*/
    }
	
    int getNextState(int currentState) {
	double rand, p0, p1;
	rand = stateRandom.nextDouble();
	int state;
	p0 = stateTrans[currentState][0];
	p1 = stateTrans[currentState][1];
	
	if (p0 <= p1) {
	    if (rand < p0)
		state = 0;
	    else state = 1;
	}
	else {
	    if (rand < p1) 
		state = 1;
	    else state = 0;
	}
	return state;
    }

    int getLossyFrame(double p0, double p1){
	double rand;
	int frame;
	rand = stateRandom.nextDouble();

	if (p0 <= p1) {
	    if (rand < p0)
		frame = 0;
	    else frame = 1;
	} 
	else {
	    if (rand < p1)
		frame = 1;
	    else frame = 0;
	}

	return frame;
    }

    
}
