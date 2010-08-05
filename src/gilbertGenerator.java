import java.io.*;
import java.util.*;

public class gilbertGenerator extends errorModel {

    /** 
     * Generates artificial gilbert trace based on the first order Markov trace.
     */

    double[][] traceTrans;
    int numBlocks;
    int[][] nextState;
    int currentState, count;
    Random gilRandom;

    public gilbertGenerator(double[][] trace_trans){
	traceTrans = trace_trans;
	//numBlocks = total;
	nextState = new int[2][2];
	nextState[0][0] = 0; nextState[0][1] = 1;
	nextState[1][0] = 1; nextState[1][1] = 0;    
	gilRandom = new Random();
	currentState = 0;
	count = 0;
    }

    public int generate(){
	int block, i;

	if (count < 3) {
	    count ++;
	    return 0;
	}

	//count = 3;
	//currentState = 0;
	//while (count <= numBlocks) {
	    block = getNextBlock(currentState);

	    //***************************
	    //System.out.println(block);
	    //***************************

	    i = currentState;
	    currentState = nextState[i][block];
	    count ++;
	    return block;
	    //}

	
    }

    public int getNextBlock(int currentState) {
	double p0, p1;
	double rand = gilRandom.nextDouble();
	int block;

	p0 = traceTrans[currentState][0];
	p1 = traceTrans[currentState][1];

	if (p0 <= p1) {
	    if (rand < p0) 
		block = 0;
	    else block = 1;
	}
	else {
	    if (rand < p1) 
		block = 1;
	    else block = 0;
	}
	//System.out.println("gilbert returns = " +  block);
	return block;
    }
}



















