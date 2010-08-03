import java.io.*;

public class testTrace {
    
    public static void main(String args[]){
	
	// get the change of state variable from the RL_file
	int changeOfState;
	getStats xism = new getStats("RL_file");
	changeOfState = xism.getC();
	System.out.println("The change of State variable is = " + changeOfState);

	// first convert the trace file into the lossy subtrace
	LossyTrace x = new LossyTrace(changeOfState, "RL_file", "lossyTraceFile");
	x.convertToLossy();
	
	// produce the probability Matrix for this lossy trace
	probabilityMatrix dawn = new probabilityMatrix("lossyTraceFile", "pattern4", 4);
	double[][] ret = dawn.generateMatrix();
	System.out.println("The probabilty matrix for lossy subtrace is = ");
	for (int i = 0; i < 16; i ++){
	    System.out.println("[ " + ret[i][0] + "    " + ret[i][1] + " ] ");
	}
	

	// convert the trace file into the state file and then convert it into a binary file
	StateGenerator x1 = new StateGenerator(changeOfState, "RL_file", "stateTraceFile");
	x1.convertToStateTrace();
	 binaryTraceGenerator x2 = new binaryTraceGenerator("stateTraceFile", "binaryStateTraceFile");
	x2.convertToBinary();

	// make a probability matrix out of the state trace
	probabilityMatrix dawn1 = new probabilityMatrix("binaryStateTraceFile", "pattern4", 4);
	double[][] ret1 = dawn1.generateMatrix();
	System.out.println("The probabilty matrix for state subtrace is = ");
	for (int i = 0; i < 16; i ++){
	    System.out.println("[ " + ret1[i][0] + "    " + ret1[i][1] + " ] ");
	}

    }
}
