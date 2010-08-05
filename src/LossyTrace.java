import java.io.*;
import java.util.*;

public class LossyTrace {

    /** 
     * This file takes in the trace file and then converts it into a binary format
     *
     * Input ==> trace file which has format 
     *
     * good   bad
     * 12     1
     * 13     34
     *
     * Output ==> lossyTrace file which has format 
     * 0010101011010000 where a lossy trace is one which concatanates the 
     * error states in which 0 is no longer than the change of state variable
     */

    int changeOfState;
    String inputFile, outputFile;

    public LossyTrace(int change, String input_file, String output_file){
	changeOfState = change;
	inputFile = input_file;
	outputFile = output_file;
    }

    public void convertToLossy(){
	try {
	    BufferedReader inpReader = new BufferedReader(new FileReader(inputFile));
	    BufferedWriter outWriter = new BufferedWriter(new FileWriter(outputFile));
	    int good, bad, state, i;
	    String line;
	    StringTokenizer str;

	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();
	    
	    while ((line = inpReader.readLine()) != null) {
		str = new StringTokenizer(line);
		
		if (str.hasMoreTokens()) {
		    good = Integer.parseInt(str.nextToken());
		    bad = Integer.parseInt(str.nextToken());
		    
		    if (good <= changeOfState) {
			state = 1;
			
			i = 0;
			while (i < good) {
			    outWriter.write("0", 0, 1);
			    i ++;
			}
			i = 0;
			while (i < bad) {
			    outWriter.write("1", 0, 1);
			    i++;
			}
		    }
		    else {
			state = 0;
			i = 0;
			while (i < bad) {
			    outWriter.write("1", 0, 1);
			    i ++;
			}
		    }
		}
	    }
	    inpReader.close();
	    outWriter.close();
	} catch (Exception e) {
	    System.out.println("Error in creation of LossyTrace: LossyTrace.java");
	    e.printStackTrace();
	}
    }
}

