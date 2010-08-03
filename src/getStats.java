import java.io.*;
import java.util.StringTokenizer;

public class getStats {


    double EBmax, EFBmax, total, FER, NumBad, NumGood, mean;
    int C, count, good, bad;
    String inputFile;
    
    public getStats (String input_file){
	inputFile = input_file;
    }

    public void getStatistics(){
       try {
	    BufferedReader inpReader = new BufferedReader(new FileReader(inputFile));
	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();
	    inpReader.readLine();

	    String line; StringTokenizer str;
	   
	    EBmax = 0;
	    EFBmax = 0;
	    total = 0;
	    FER = 0;
	    count = 0;
	    NumBad = 0;
	    NumGood = 0;
	    mean = 0;
	    C = 0;

	    while ((line = inpReader.readLine()) != null) {
		
		str = new StringTokenizer(line);
		
		if (str.hasMoreTokens()) {
		    good = Integer.parseInt(str.nextToken());
		    bad = Integer.parseInt(str.nextToken());
		    total += (double) good + bad;
		    if (bad > EBmax) EBmax = (double) bad;
		    if (good > EFBmax) EFBmax = (double) good;
		    NumBad += (double) bad;
		    NumGood += (double) good;
		    count ++;
		}
	    }

	    FER = NumBad/total;
	    double mean_bad = (double) NumBad/count;
	    double mean_good = (double) NumBad/count;

	    inpReader.close();

	    BufferedReader inpReader1 = new BufferedReader(new FileReader(inputFile));
	    
	    inpReader1.readLine();
	    inpReader1.readLine();
	    inpReader1.readLine();
	    inpReader1.readLine();
	    
	    int term_bad = 0;
	    int term_good = 0;
	    double stdev_good = 0;
	    double stdev_bad = 0;

	    while ((line = inpReader1.readLine()) != null) {
		str = new StringTokenizer(line);

		if (str.hasMoreTokens()) {
		    good = Integer.parseInt(str.nextToken());
		    bad = Integer.parseInt(str.nextToken());
		    
		    term_bad += (double) Math.pow((bad - mean_bad), 2);
		    term_good += (double) Math.pow((good - mean_good), 2);
		}
	    }

	    inpReader1.close();

	    stdev_bad = (double) Math.floor(Math.sqrt(term_bad/count));
	    stdev_good = (double) Math.floor(Math.sqrt(term_good/count));
	    C = (int) Math.floor(mean_bad + stdev_bad);

       } catch (Exception e) {
	   e.printStackTrace();
	   System.out.println("Exception in getStats");
       }

    }

    public int getC(){
	return C;
    }

    public double getFER(){
	//System.out.println("Return FER = " + FER);
	return FER;
    }

}
	







