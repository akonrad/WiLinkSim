import java.io.*;
import java.util.*;

public class mtaGenerator extends errorModel {
    
    double[][] lossyTrans;
    double badPar, goodPar;
    int numBlocks;
    Random blockRandom, parRandom;
    int[][] nextState;

    public mtaGenerator() {/** double[][] lossy_trans, double bad_par, double good_par, int total){
	lossyTrans = lossy_trans;
	badPar = bad_par;
	goodPar = good_par;
	numBlocks = total;
	blockRandom = new Random();
	parRandom = new Random();
	nextState = new int[16][2];
	nextState[0][0] = 0;	nextState[0][1] = 1;	nextState[1][0] = 2;	nextState[1][1] = 3;	nextState[2][0] = 4;	nextState[2][1] = 5;
	nextState[3][0] = 6;	nextState[3][1] = 7;	nextState[4][0] = 8;	nextState[4][1] = 9;	nextState[5][0] = 10;	nextState[5][1] = 11;	
	nextState[6][0] = 12;	nextState[6][1] = 13;	nextState[7][0] = 14;	nextState[7][1] = 15;	nextState[8][0] = 0;	nextState[8][1] = 1;
	nextState[9][0] = 2;	nextState[9][1] = 3;	nextState[10][0] = 4;	nextState[10][1] = 5;	nextState[11][0] = 6;   nextState[11][1] = 7;
	nextState[12][0] = 8;   nextState[12][1] = 9;	nextState[13][0] = 10;	nextState[13][1] = 11;	nextState[14][0] = 12;	nextState[14][1] = 13;
	nextState[15][0] = 14;	nextState[15][1] = 15;
			   */
    }

    public int generate(){
	/**
	int currentState, count, block;
	double len;
	currentState = 0;
	
	//******************************
	System.out.println("0000");
	//******************************

	count = 4;

	while (count < numBlocks) {
	    len = getLen(goodPar);
	    int c = 0;
	    int i;

	    while ((c < len) && (count < numBlocks)){
		//**************************
		System.out.println("0");
		//**************************
		c ++;
		count ++;
	    }

	    len = getLen(badPar);
	    c = 0;
	    while ((c < len) && (count < numBlocks)){
		block = getNextBlock(currentState);
		//**************************
		System.out.println(block);
		//**************************
		i = currentState;
		currentState = nextState[i][block];
		c++; count ++;
	    }

	}
	*/
	return 0;
    }

    public double getLen(double par){
	/**
	double u = parRandom.nextDouble();
	// ***********************
	return 1;
	// ***********************
	*/
	return 1;
    }

    public int getNextBlock(int currentState){
	/**
	double p0, p1, rand;
	int block;
	
	p0 = lossyTrans[currentState][0];
	p1 = lossyTrans[currentState][1];
	rand = blockRandom.nextDouble();
	
	if (p0 <= p1) {
	    if (rand < p0)
		block =0;
	    else block = 1;
	}
	else {
	    if (rand < p1) 
		block = 1;
	    else block = 0;
	}
	return block;*/
	return 0;
    }
}
	    
		
