import java.io.*;
import java.util.*;

public class m3Generator extends errorModel {

    /**
     * This object takes in a probability matrix for the lossy state transitions and one for the state trace transitions. It accordingly sets up a state
     * machine, which decides whether to corrupt a given frame or not.
     */

    int numBlocks;
    double[][] lossyTrans, stateTrans;
    int[][] nextState;
    Random lossyRandom, stateRandom;
    int state, stateValue, lossyState;

    /**
     * The constructor takes in the two matrices and total number of blocks
     */
    public m3Generator(double[][] lossy_trans, double[][] state_trans){
	lossyTrans = lossy_trans;
	stateTrans = state_trans;
	//numBlocks = total;
	lossyRandom = new Random();
	stateRandom = new Random();
	nextState = new int[16][2];
	state = 0;
	stateValue = 0;
	lossyState = 1;
	nextState[0][0] = 0;	nextState[0][1] = 1;	nextState[1][0] = 2;	nextState[1][1] = 3;	nextState[2][0] = 4;	nextState[2][1] = 5;
	nextState[3][0] = 6;	nextState[3][1] = 7;	nextState[4][0] = 8;	nextState[4][1] = 9;	nextState[5][0] = 10;	nextState[5][1] = 11;	
	nextState[6][0] = 12;	nextState[6][1] = 13;	nextState[7][0] = 14;	nextState[7][1] = 15;	nextState[8][0] = 0;	nextState[8][1] = 1;
	nextState[9][0] = 2;	nextState[9][1] = 3;	nextState[10][0] = 4;	nextState[10][1] = 5;	nextState[11][0] = 6;   nextState[11][1] = 7;
	nextState[12][0] = 8;   nextState[12][1] = 9;	nextState[13][0] = 10;	nextState[13][1] = 11;	nextState[14][0] = 12;	nextState[14][1] = 13;
	nextState[15][0] = 14;	nextState[15][1] = 15;
    }

    
    /** 
     * The error model generator 
     */
    public int generate(){
	int count, currentState, currentLossyState, frame;
	count = 0;
	//state= 0;
	
	//lossyState = 1;
	currentState = state;
	currentLossyState = lossyState;
	stateValue = getNextState(currentState);
	
	if (stateValue == 0) {
	    frame = 0;
	}
	else {
	    frame = getLossyFrame(currentLossyState);
	    lossyState = nextState[currentLossyState][frame];
	}
	
	// ************* Change this to reflect some video stream *************
	//System.out.println(frame);
	// *********** Change ends **************
	
	state = nextState[currentState][stateValue];
	//count ++;
	return frame;
	//}
    }
    
    public int getLossyFrame(int currentLossyState){
	double prob0, prob1, rand;
	int frame; 
	prob0 = lossyTrans[currentLossyState][0];
	prob1 = lossyTrans[currentLossyState][1];
	rand = lossyRandom.nextDouble();
	
	if (prob0 <= prob1){
	    if (rand < prob0) 
		frame = 0;
	    else frame = 1;
	}
	else {
	    if (rand < prob1)
		frame = 1;
	    else frame = 0;
	}
	
	return frame;
    }

    public int getNextState(int currentState){
	double prob0, prob1, rand;
	int state;
	prob0 = stateTrans[currentState][0];
	prob1 = stateTrans[currentState][1];
	rand = stateRandom.nextDouble();
	
	if (prob0 <= prob1) {
	    if (rand < prob0)
		state = 0;
	    else state = 1;
	}
	else {
	    if (rand < prob1)
		state = 1;
	    else state = 0;
	}
	
	return state;
    }
    
    public int getCurrentState() {
	return stateValue;
    }

}
    
