import java.io.*;
import java.util.StringTokenizer;

public class binaryTraceGenerator {

    /** Takes in a file with the format -
     *
     * good (or something else)      bad (yadadadadada)
     * 
     *
     * 21                              23
     * 4                               314
     *
     * and returns a binary file which simply lists all the zeroes and ones
     */

    int good, bad;
    String inputFile, outputFile;
    
    public binaryTraceGenerator(String input_file, String output_file) {
	inputFile = input_file;
	outputFile = output_file; 
    }

    public void convertToBinary(){
	try {
	    BufferedReader inpReader = new BufferedReader(new FileReader(inputFile));
	    BufferedWriter outWriter = new BufferedWriter(new FileWriter(outputFile));
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

		    for (int i = 0; i < good; i ++)
			outWriter.write("0");
		    
		    for (int i = 0; i < bad; i ++)
			outWriter.write("1");
		}
	    }

	    inpReader.close();
	    outWriter.close();

	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("error in binaryTraceGenerator.java");
	}

    }

}










