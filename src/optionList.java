import java.io.*;
import java.util.StringTokenizer;

public class optionList {

    public int dataLength, headerLength, changeOfState, transmission_time, wirelessFECn, wirelessFECm, ipFECn, ipFECm;
    public double[][] stateMatrix, lossyMatrix;
    public String model, wirelessMode, ipErrorModel, wirelessErrorModel, ipMode;
    public boolean ipModel, wirelessModel, ipFeedback, wirelessFeedback, ipFEC, wirelessFEC;
    public double FER;

    public optionList(String name){

	try {
	    BufferedReader optionReader = new BufferedReader(new FileReader(name));
	    String line;
	    StringTokenizer str;

	    /**
	     * 1) Get length of header and data packets
	     */
	    
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<Network_Data_Length>")){
		    dataLength = Integer.parseInt(str.nextToken());
		    //break;
		}
	    }
	    optionReader = new BufferedReader(new FileReader(name));

	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<Network_Header_Length>")){
		    headerLength = Integer.parseInt(str.nextToken());
		    //break;
		}
	    }
	    

	    /**
	     * 1.1) Find out whether the ip model is chosen 
	     */
	    optionReader = new BufferedReader(new FileReader(name));
	    String ipE;
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<IP_Model>")) {
		    ipE = str.nextToken();
		    if (ipE.equals("TRUE"))
			{
			    ipModel = true;
			}
		    else ipModel = false;
		    //break;
		}
	    }

	    /**
	     * 1.2) Find out whether the wireless modelling is chosen 
	     */
	    optionReader = new BufferedReader(new FileReader(name));
	    String wirelessE;
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<Wireless_Model>")) {
		    wirelessE = str.nextToken();
		    if (wirelessE.equals("TRUE"))
			{
			    wirelessModel = true;
			}
		    else wirelessModel = false;
		    //break;
		}
	    }

	    /** 
	     * 2.1) Get the wireless error model name if there is one
	     */
	    
	    if (wirelessModel)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<WirelessErrorModel>")){
			    wirelessErrorModel = str.nextToken();
			    //break;
			}
		    }
		}
	    else wirelessErrorModel = "NONE";

	    /** 
	     * 3) If wireless modelling is used then get the mode - error or delay
	     */

	    if (wirelessModel)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<Wireless_Mode>")){
			    wirelessMode = str.nextToken();
			    //break;
			}
		    }
		}
	    else wirelessMode = "NONE";
	    
	    /**
	     * 3. 1) If the option of error model for IP layer is chosen, then find out
	     * which one
	     */

	    if (ipModel) {
		optionReader = new BufferedReader(new FileReader(name));
		while ((line = optionReader.readLine()) != null) {
		    str = new StringTokenizer(line);
		    if (str.nextToken().equals("<IpErrorModel>")) {
			ipErrorModel = str.nextToken();
			//break;
		    }
		}
	    }
	    else ipErrorModel = "NONE";


	    /**
	     * If the option for losses in IP layer is chosed then find out what it is.
	     */

	    if (ipModel) {
		optionReader = new BufferedReader(new FileReader(name));
		while ((line = optionReader.readLine()) != null) {
		    str = new StringTokenizer(line);
		    if (str.nextToken().equals("<IP_Mode>")) {
			ipMode = str.nextToken();
			//break;
		    }
		}
	    }
	    else ipMode = "NONE";
	    
	    
	    /**
	     * 3.7) Find out whether feedback is supported if the wireless modelling is chosen 
	     */
	    if (wirelessModel)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    String wfeedback;
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<Wireless_Feedback>")) {
			    wfeedback = str.nextToken();
			    if (wfeedback.equals("TRUE"))
				{
				    wirelessFeedback = true;
				}
			    else wirelessFeedback = false;
			    //break;
			}
		    }
		}
	    else wirelessFeedback = false;
	    

	    /**
	     * 3.8) Find out whether feedback is supported if IP modelling is chosen 
	     */
	    if (ipModel)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    String IPfeedback;
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<IP_Feedback>")) {
			    IPfeedback = str.nextToken();
			    if (IPfeedback.equals("TRUE"))
				{
				    ipFeedback = true;
				}
			    else ipFeedback = false;
			    //break;
			}
		    }
		}
	    else ipFeedback = false;

	    
	    /** 
	     * 4) get the change of State value
	     */
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<ChangeOfState>")){
		    changeOfState = Integer.parseInt(str.nextToken());
		    //break;
		}
	    }
 
	    /** 
	     * 4.1) get the transmission_Time value
	     */
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<Network_Transmission_Time>")){
		    transmission_time = Integer.parseInt(str.nextToken());
		    //break;
		}
	    }
	    /** 
	     * 4.2) Check to see if ipFEC is required
	     */
	    ipFEC = false;
	    optionReader = new BufferedReader(new FileReader(name));
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<IP_FEC>")){
		    ipFEC = (Boolean.valueOf(str.nextToken()).booleanValue());
		}
	    }
	    
	    // if ipFEC is true then we find out m and n values
	    if (ipFEC)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<IP_FEC_NVALUE>")){
			    ipFECn = Integer.parseInt(str.nextToken());
			}
		    }

		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<IP_FEC_MVALUE>")){
			    ipFECm = Integer.parseInt(str.nextToken());
			}
		    }
		    //System.out.println("IP FEC values n = " + ipFECn + " m = " + ipFECm);    
		}
	
	    /**
	     * 4.3) Check to see if wirelessFEC is required
	     */ 
	    wirelessFEC = false;
	    optionReader = new BufferedReader(new FileReader(name));
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<WIRELESS_FEC>")){
		    wirelessFEC = (Boolean.valueOf(str.nextToken())).booleanValue();
		}
	    }
	    
	    // if ipFEC is true then we find out m and n values
	    if (wirelessFEC)
		{
		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<WIRELESS_FEC_NVALUE>")){
			    wirelessFECn = Integer.parseInt(str.nextToken());
			}
		    }

		    optionReader = new BufferedReader(new FileReader(name));
		    while ((line = optionReader.readLine()) != null) {
			str = new StringTokenizer(line);
			if (str.nextToken().equals("<WIRELESS_FEC_MVALUE>")){
			    wirelessFECm = Integer.parseInt(str.nextToken());
			}
		    }
		    //System.out.println("Wireless FEC values n = " + wirelessFECn + " m = " + wirelessFECm);    
		}
	    


	    /**
	     * 4.5) Get the FER value
	     */
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<FER>")){
		    FER = Double.parseDouble(str.nextToken());
		    //break;
		}
	    }


	    /**
	     * 5) get the lossyMatrix
	     */
	    // get the number of elements in the matrix first
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    int count = 0;
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<LossyMatrix>")){
		    while ((line = optionReader.readLine()) != null){
			str = new StringTokenizer(line);
			if ((str.nextToken()).equals("</LossyMatrix>"))
			    break;
			count += 1;
		    }
		    //break;
		}
	    }

	    lossyMatrix = new double[count][2];
	    
	    // now start filling in the matrix
	    optionReader = new BufferedReader(new FileReader(name));

	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<LossyMatrix>")) {
		    
		    for (int i = 0; i < count; i ++) {
			line = optionReader.readLine();
			str = new StringTokenizer(line);
			lossyMatrix[i][0] = Double.parseDouble(str.nextToken());
			lossyMatrix[i][1] = Double.parseDouble(str.nextToken());
		    }
		    //break;
		}
	    }
	    
	    /** 
	     * Get the stateMatrix filled
	     */
	    // get the number of elements in the matrix first
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    count = 0;
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<StateMatrix>")){
		    while ((line = optionReader.readLine()) != null){
			str = new StringTokenizer(line);
			if ((str.nextToken()).equals("</StateMatrix>"))
			    break;
			count += 1;
		    }
		    //break;
		}
	    }

	    stateMatrix = new double[count][2];
	    
	    // now start filling in the matrix
	    optionReader = new BufferedReader(new FileReader(name));
	    
	    while ((line = optionReader.readLine()) != null) {
		str = new StringTokenizer(line);
		if (str.nextToken().equals("<StateMatrix>")) {
		    
		    for (int i = 0; i < count; i ++) {
			line = optionReader.readLine();
			str = new StringTokenizer(line);
			stateMatrix[i][0] = Double.parseDouble(str.nextToken());
			stateMatrix[i][1] = Double.parseDouble(str.nextToken());
		    }
		    //break;
		}
	    }	    

	    optionReader.close();
	} catch (Exception e) {
	    System.out.println("Error in the optionList.java constructor");
	    e.printStackTrace();
	}
    }

    public void print(){
	
	System.out.println();
	System.out.println(" Data length is " + dataLength);
	System.out.println(" Header length is " + headerLength);
	System.out.println(" Change of State Variable " + changeOfState);

	System.out.println();
	System.out.println(" IP modelling is " + ipModel);
	System.out.println(" IP Error Model is " + ipErrorModel);
	System.out.println(" IP Mode is " + ipMode);
	System.out.println(" IP Feedback is " + ipFeedback);

	System.out.println();
	System.out.println(" Wireless modelling is " + wirelessModel);
	System.out.println(" Wireless Error Model is " + wirelessErrorModel);
	System.out.println(" Wireless Mode is " + wirelessMode);
	System.out.println(" Wireless Feedback is " + wirelessFeedback);

	System.out.println();
	System.out.println("the lossy Matrix is as follows = ");
	for (int i = 0; i < lossyMatrix.length; i ++)
	    System.out.println("[ " + lossyMatrix[i][0] + "\t" + lossyMatrix[i][1] + "]");
	
	 System.out.println();
	 System.out.println("the state Matrix is as follows = ");
	 for (int i = 0; i < stateMatrix.length; i ++)
	     System.out.println("[ " + stateMatrix[i][0] + "\t" + stateMatrix[i][1] + "]");

	 
    }
}



