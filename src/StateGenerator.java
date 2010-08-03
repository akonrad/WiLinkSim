import java.io.*;
import java.util.*;

public class StateGenerator {

    /** 
     * This file takes in the trace file and then converts it into a binary format
     *
     * Input ==> trace file which has format 
     *
     * good   bad
     * 12     1
     * 13     34
     *
     * Output ==> file which has format
     * 
     * error_free_burst  error_burst
     * 
     * 12                 1
     * 23                 3
     * 
     * So basically, it just compares the length of the good section with
     * change of state variable. If it is less, then we have to add that to
     * the error_burst length.
     */

    int changeOfState;
    String inputFile, outputFile;

    public StateGenerator(int change, String input_file, String output_file){
	changeOfState = change;
	inputFile = input_file;
	outputFile = output_file;
    }

    public void convertToStateTrace(){
	try {
	    BufferedReader inpReader = new BufferedReader(new FileReader(inputFile));
	    BufferedWriter outWriter = new BufferedWriter(new FileWriter(outputFile));

	    int good, bad, good_state, bad_state, sw;
	    String line;
	    StringTokenizer str;
	    
	    good = 0; bad = 0; good_state = 0; bad_state = 0; sw = 0;
	    
	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();
	    
	    outWriter.newLine();
	    outWriter.write("Error_free_burst\tError_burst");
	    outWriter.newLine();
	    outWriter.newLine();
	    outWriter.newLine();

	    while ((line = inpReader.readLine()) != null) {

		str = new StringTokenizer(line);

		if (str.hasMoreTokens()) {
		    good = Integer.parseInt(str.nextToken());
		    bad = Integer.parseInt(str.nextToken());
		    
		    if (good > changeOfState) {
			sw = 0;
			if (bad_state > 0) {
			    sw = 1;
			    outWriter.write(good_state + "\t" + bad_state);
			    outWriter.newLine();
			}
			bad_state = bad;
			good_state = good;
		    }
		    else {
			sw = 0;
			if (bad_state == 0){
			    bad_state = bad;
			    good_state = good;
			}
			else bad_state += bad + good;
		    }
		}
	    }

	    if (sw == 0) {
		outWriter.write(good_state + "\t" + bad_state);
		outWriter.newLine();
	    }

	    outWriter.close();
	    inpReader.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("error in StateGenerator.java");
	}

    }

}
