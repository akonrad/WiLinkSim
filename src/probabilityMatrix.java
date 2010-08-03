import java.io.*;
import java.util.*;

public class probabilityMatrix {
    
    /**
     * This class is responsible for creating a probability matrix from a 
     * trace. It essentially does pattern matching to a degree specified by 
     * the user.The inputs are:
     * 
     * trace_file ==> a binary file which simply lists the corrupt and 
     * non-corrupt frames.
     * k ==> the degree to which pattern matching is to be carried out.
     * pattern_file ==> the file which lists the patterns to be matched. 
     * 
     * The output is a two-dimensional array of the same length as the pattern
     * file.
     */

    String patternFile, traceFile;
    int degree;

    public probabilityMatrix(String trace_File, String pattern_File, int k){
	traceFile = trace_File; 
	patternFile = pattern_File;
	degree = k;
    }
    

    /**
     * Opens a given file and returns the bufferedReader for that particular 
     * file
     */

    public BufferedReader openFile(String file_name){
	try {
	    FileReader temp1 = new FileReader(file_name);
	    BufferedReader temp = new BufferedReader(temp1);
	    return temp;
	} catch (Exception e) {
	    System.out.println("Error while opening file: " + file_name + " in openFile in probabilityMatrix");
	    return null;
	}
    }


    /** 
     * The function which calculates the matrix. It loads one pattern from the pattern file and then finds out how many sequences in the trace_file match
     * the given pattern. For each sequence that matches the pattern, the block following the sequence is found out, and the statistics are collocted in 
     * order to find out the probability that a given sequence will be followed by a corrupt or correct block. 
     */
    public double[][] generateMatrix(){
	try {
	    
	    BufferedReader patternReader, traceReader;            // the handlers for the two files - the pattern file and the trace file
	    int totalSamples, numOfStates;                        // numOfStates indicates the number of possible values a sequence can have
	    String line;                                          
	    patternReader = openFile(patternFile);
	    
	    
	    // totalSamples = findTotalBlocks(traceFile);           // ******** Do we even need this??? ***************
	    numOfStates = (int) Math.pow(2, degree);
	    //System.out.println("number of states = " + numOfStates);

	    // This matrix indicates the number of times the sequence indicated by i is followed by j (0 or 1)
	    int[][] occurences = new int[numOfStates][2];

	    // This matrix indicates the probability that the sequence indicated by i is followed by j (0 or 1)
	    double[][] probabilities = new double[numOfStates][2];     


	    // indicates which pattern we are talking about. for example, for degree 4, state 0 means the pattern is 0000 and state 15 denotes a pattern of1111.
	    int state = 0; 

	    // Go through all the lines in the pattern file, traversing through the trace file once for every pattern in the pattern file
	    //while (state <= numOfStates){
	    while ((line = patternReader.readLine()) != null) {
		traceReader = openFile(traceFile);                     // opening the file and searching for the current pattern
		
		/** 
		 * 1)  Read the pattern on this line of the pattern file into an array for comparision 
		 */
		StringTokenizer currentLine = new StringTokenizer(line);
		int count;                                    //  count is used as an index for the array
		int[] currentPattern = new int[degree];
		for (count = (degree - 1); count >= 0; count --) {
		    currentPattern[count] = Integer.parseInt(currentLine.nextToken());
		}
		
		int endOfFile = 0;                            // indicates the end of the trace file, is set to 1 inside the loop once it is over
		
		/** 
		 * 2) Read the first degree blocks from the traceFile, once in the loop, they will simply be swapped
		 */		
		int next = 0;
		int[] currentTrace = new int[degree];
		count = degree - 1;
		while (count >= 0) {
		    if ((next = traceReader.read()) != -1){
			if (next == 48)
			    currentTrace[count] = 0;
			else currentTrace[count] = 1;
		    }
	       	    else { 
			endOfFile = 1;
			break;
		    }
		    count --;
		}
	        
		/** 
		 * 3) Read the trace_file and compare patterns 
		 */
		while (endOfFile == 0) {
		    
		    /** 
		     * 4)  compare the patterns and see if they match 
		     */
		    if (comparePatterns(currentPattern, currentTrace)) {
			if ((next = traceReader.read()) != -1){
			    /** 
			     * 5) if they do match, then note down which binary number in the trace comes up next 
			     */
			    if (next == 48)
				occurences[state][0] ++;
			    else occurences[state][1] ++;
			}
			else {
			    endOfFile = 1;
			    break;
			}
		    }
		    else {
			next = traceReader.read();
			if (next == -1){
			    endOfFile = 1;
			    break;
			}
		    } 

		    
		    /** 
		     * 6) Regardless of whether the patterns match or not, the trace-file digits must be swapped. Note the digits are swapped in 
		     * reverse order.
		     */
		    for (int i = degree - 1; i > 0; i-- ) {
			currentTrace[i] = currentTrace[i-1];
		    }
		    if (next == 48)
			currentTrace[0] = 0;
		    else currentTrace[0] = 1;

		    for (int i = 3; i >= 0; i --) {
		    }
		}

		// We have gone through one traversal of the trace_file, so we must search for a new pattern
		state ++;
	    }

	    /** 
	     * 7) Calculate the probabilites for the occurences 
	     */
	    int total = 0;
	    for (int i = 0; i < numOfStates; i ++) {
		
		total = occurences[i][0] + occurences[i][1];
		if (total != 0){
		    probabilities[i][0] = (double) occurences[i][0]/total;
		    probabilities[i][1] = (double) occurences[i][1]/total;
		}
	    }

	    return probabilities;

	} catch (Exception e) {
	    System.out.println("Error in calculate function of probMatrix");
	    //e.printStackTrace();
	    return null;
	}
    }

    // Compares the two arrays to see if the pattern matches with the given sequence
    boolean comparePatterns(int[] pattern, int[] trace){
	for (int i = 0; i < degree; i ++) {
	    if (pattern[i] != trace[i]){
		return false;
	    }
	}
	return true;
    }

}










