import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class senderThread extends Thread {

    /**
     * This class reads text from a file, puts the data into a buffer. The fragmenter picks them up from the buffer and sends them to the RLE.
     * Takes in the packetbuffer and locking object as an argument. Also reads the end to end feedback, if there is any.
     */

    // File reader for input file
    FileInputStream file_stream;
    
    FileWriter sender_output, feedback_file;

    Timestamp timer;

    // constants for packet size
    int HEADER_SIZE = 40;        // total header size after considering all protocols involved = RTP + UDP + IP + PPP
    int DATA_SIZE = 216;           // size of data segment in each packet.
    int FEEDBACK_PACKET_OFFSET = 0;    // arbitrary values
    int FEEDBACK_PACKET_DATA_OFFSET = 5; // arbitrary values

    // Random generator
    Random rand;

    boolean ipLosses = false;    // Shows if losses at ip level are required
    boolean ipDelayOrError = false;  // True denotes delay and false denotes error
    boolean ipFeedback = false;
    LinkedList unfragmented_queue, feedback_queue;
    Object unfragmented_lock, feedback_lock;
    errorModel lossGenerator;
    int linkCondition = 0;     // 0 denotes a good link and 1 denotes a bad link. Set after last feedback receipt
    int totalbytes = 0;
    int packetnum = 0;

    boolean ipFEC;
    int ipFECn, ipFECm;
    int ipPacketCounter = -1;
    int fecID = 0;

    /**
     * Constructor which initializes the file reader and sets up other variables such as the lossGenerator. Also sets the output buffer
     */
    public senderThread(String input_file, optionList opt, LinkedList queue, Object lock, LinkedList queue2, Object lock2, String sender_dump, String feedback_dump)
    {
	try {
	    unfragmented_queue = queue;
	    unfragmented_lock = lock;
	    ipLosses = opt.ipModel;
	    feedback_lock = lock2;
	    feedback_queue = queue2;
	    rand = new Random();
	    ipFEC = opt.ipFEC;

	    if (ipFEC) {
		ipFECn = opt.ipFECn;
		ipFECm = opt.ipFECm;
	    }
	    if (ipLosses)
		{
		    if (opt.ipMode.equals("ERROR"))
			ipDelayOrError = false;
		    else ipDelayOrError = true;
		    ipFeedback = opt.ipFeedback;
		    if (opt.ipErrorModel.equals("HMM"))
			lossGenerator = new hmmGenerator(opt.FER, opt.stateMatrix);
		    else if (opt.ipErrorModel.equals("M3"))
			lossGenerator = new m3Generator(opt.lossyMatrix, opt.stateMatrix);
		    else if (opt.ipErrorModel.equals("GILBERT"))
			lossGenerator = new gilbertGenerator(opt.lossyMatrix);
		    else if (opt.ipErrorModel.equals("MTA"))
			lossGenerator = new mtaGenerator();
		}

	    file_stream = new FileInputStream(input_file);
	    sender_output = new FileWriter(sender_dump);

	    //sender_output.write("Timestamp\t\t\tByte Number\t Sequence Number\n");
	    //sender_output.write("---------\t\t\t-----------\t ---------------\n");
	    sender_output.flush();

	    if (ipFeedback)
		{
		    feedback_file = new FileWriter(feedback_dump);
		    //feedback_file.write("Timestamp\t\t\tSequence Number\tLink Condition\n");
 		    //feedback_file.write("---------\t\t\t---------------\t--------------\n");
		    feedback_file.flush();
		}
	} catch (Exception e){
	    e.printStackTrace();
	}
    }

    /**
     * Read the file and packetize it. Send it to the unfragmented queue.
     */
    public void run(){

	int bytes_read;
	byte[] data = new byte[DATA_SIZE];
	byte[] modifiedPacket;

	try {
	    //while (true) {

		// 1) Read the data from the file into the packet
		
		SimonTest.log(SimonTest.senderFlag, "Reading the input file into packets:\n");
		int packetsRead = -1;

	    while ((bytes_read = file_stream.read(data)) != -1)
		    {
			totalbytes += bytes_read;    // increment bytes read to put in the sender file.

			// 1.5) Write into the dump file

			timer = new Timestamp((new Date()).getTime());
			sender_output.write(timer.toString() + "\t\t" + totalbytes + "\t\t" + packetnum + "\n");
			sender_output.flush();
			
			packetsRead++;
			SimonTest.log(SimonTest.senderFlag, "Packet " + packetsRead + ", raw data:");
			SimonTest.log(SimonTest.senderFlag, SimonTest.bytesToString(data));

			// 2) Add checksum and error correction to this packet.
			//    Also writes into the file required.
			modifiedPacket = addCorrection(data, bytes_read);
			SimonTest.log(SimonTest.senderFlag, "Packet " + packetsRead + ", after correction:");
			SimonTest.log(SimonTest.senderFlag, SimonTest.packetToString(modifiedPacket, HEADER_SIZE));

			// 3) Run the packet through the error generator now.
			//    Might get null packet if it is dropped
			modifiedPacket = messUpPacket(modifiedPacket);
			SimonTest.log(SimonTest.senderFlag, "Packet " + packetsRead + ", after error generation:");
			SimonTest.log(SimonTest.senderFlag, SimonTest.packetToString(modifiedPacket, HEADER_SIZE) + "\n");

			// 4) Send the packet.
			if (modifiedPacket != null)
			    {
				sendPacket(modifiedPacket);
			    }

			// 5) Check for feedback and set the linkcondition
			if (ipFeedback)
			    processFeedback();

		        packetnum += 1;

//  			if (packetnum == 127)
//  			    packetnum = 0;
		    }
		System.out.println("termination number is " + packetnum);
		startup.terminationNum = packetnum;
		startup.termination = true;
		//}
	} catch (Exception e){
	    e.printStackTrace();
	}

    }

    /**
     * Process the feedback received from the feedback queue and set the value of linkCondition
     */
    public void processFeedback()
    {
	try {
	    byte[] feedback;
	    LinkedList clone;
	    byte feedbackNum;
	    String tempCondition;

	    synchronized (startup.E2E_feedback_wake) {


		while (startup.E2E_feedback_buffer.size() == 0)
		    startup.E2E_feedback_wake.wait();

		if (startup.E2E_feedback_buffer.size() != 0)
		    {

			clone = (LinkedList) startup.E2E_feedback_buffer.clone();
			startup.E2E_feedback_buffer.clear();

			while (clone.size() != 0)
			    {
				feedback = (byte[]) clone.removeFirst();

				// get packet number and the linkcondition

				linkCondition = (int) feedback[FEEDBACK_PACKET_DATA_OFFSET];
				feedbackNum = feedback[FEEDBACK_PACKET_OFFSET];

				// write into the file
				timer = new Timestamp((new Date()).getTime());
				if (linkCondition == 0) {
				    tempCondition = "GOOD";
				    //System.out.println("linkCondition is Good");
				}
				else {
				    tempCondition = "BAD";
				    //System.out.println("linkCondition is Bad");
				}

				feedback_file.write(timer + "\t\t" + feedbackNum + "\t\t" + tempCondition + "\n");
			    }
		    }
	    }
	} catch (Exception e){
	    e.printStackTrace();
	}
    }

    /**
     * Add error correction to the packet if there is feedback for bad conditions. First construct the header and checksum on the header.
     */
    public byte[] addCorrection(byte[] data_seg, int size_read){
	byte[] fecPacket;
	byte[] packetNumber = new byte[4];
	// 1) Create a header and populate it with random stuff

	byte[] ppp_packet = new byte[HEADER_SIZE + size_read];

	// fill header with random stuff
	for (int i = 0; i < HEADER_SIZE; i ++) {
	    if (rand.nextBoolean())
		ppp_packet[i] = 0;
	    else ppp_packet[i] = 1;
	}
	ppp_packet[startup.PACKET_ERROR_BYTE] = 0;   // This is a hack to get around checksum, setting it to 0 to denote correct packet.

	for (int i = HEADER_SIZE; i < ppp_packet.length; i ++) {

	    ppp_packet[i] = data_seg[i - HEADER_SIZE];

	}

	// 1.5) Put Packet number into packet
	packetNumber = getByteArray(packetnum);
	//ppp_packet[0] = packetnum;
	ppp_packet[4] = packetNumber[0];
	ppp_packet[5] = packetNumber[1];
	ppp_packet[6] = packetNumber[2];
	ppp_packet[7] = packetNumber[3];

	// 2) Create a checksum and put it at the start of the header. Checksum only checks for stuff in the data segment

	// 3) Check to see if you need FEC, done by looking at linkCondition

	//if (linkCondition == 1)
	//  fecPacket = addFEC(ppp_packet);
	//else fecPacket = ppp_packet;

	// set these bits initially to 0, to change based on modelling
	ppp_packet[startup.FEC_ID_BYTE] = (byte) fecID;
	ppp_packet[startup.FEC_REDUNDANCY_PACKET] = (byte) 0;
	ppp_packet[startup.PACKET_ERROR_BYTE] = (byte) 0;

	return ppp_packet;

    }

    /**
     * Adds FEC
     */
    public void addFEC()
    {
	byte[] FECpacket;

	if (ipPacketCounter % ipFECn == (ipFECn - 1)) {

	    for (int i = 0; i < ipFECm; i ++)
		    {
			FECpacket = new byte[HEADER_SIZE + DATA_SIZE];
			FECpacket[startup.PACKET_ERROR_BYTE] = (byte) 0;
			FECpacket[startup.FEC_REDUNDANCY_PACKET] = (byte) 1;
			FECpacket[startup.FEC_ID_BYTE] = (byte) fecID;

			FECpacket = messUpPacket(FECpacket);
			if (FECpacket != null)
			    startup.packet_buffer.add(FECpacket);
		    }
	    fecID += 1;
	    if (fecID >= 127)
		fecID = 0;
	}
    }

    /**
     * Changes the packet if the error model predicts corruption. if the error model is delay, it returns null to signify packet drop
     */
    public byte[] messUpPacket(byte[] origPacket){
	int error = 0;

	// 1) if there are no error model required for ip level, then simply return the old packet.
	if (!ipLosses)
	    return origPacket;
	else error = lossGenerator.generate();

	if (error == 0) {
	    System.out.println("no error added to # " + packetnum + " ******************");
	    startup.terminationNum = packetnum;
	    return origPacket;
	}
	else if (ipDelayOrError) //ipDelay true if delay, false for error
	    return null;
	else
	    {
		System.out.println("corrupting packet # " + packetnum + " ***************");
		origPacket[startup.PACKET_ERROR_BYTE] = 1;
		// testing to see if i can get away with out checksum
		/**
		   if (rand.nextBoolean())
		   origPacket[i] = (byte) 0;
		*/
		startup.terminationNum = packetnum;
		return origPacket;
	    }
    }


    /**
     * This method places the packet on the shared buffer for the fragmenter break up
     */
    public void sendPacket(byte[] data_seg)
    {
	/** Debugging output */
	//String S = new String(data_seg, HEADER_SIZE, (data_seg.length - HEADER_SIZE));

	//System.out.print("*** " + S);

	synchronized (startup.frag_wake) {
	    ipPacketCounter += 1;
	    startup.packet_buffer.addLast(data_seg);

	    if (ipFEC && linkCondition == 1) {
		//data_seg[startup.FEC_ID_BYTE] = (byte) fecID;
		addFEC();
	    }

	    startup.frag_wake.notifyAll();
	}

    }

    public byte[] getByteArray(int num)
    {
	int temp = num;
	byte[] returnArray = new byte[4];

	temp = temp & 0x000ff;

	returnArray[0] = (byte) temp;

	temp = num & 0x00ff00;
	temp = temp >> 8;

	returnArray[1] = (byte) temp;

	temp = num & 0x0ff0000;
	temp = temp >> 16;

	returnArray[2] = (byte) temp;

	temp = num & 0xff000000;
	temp = temp >> 24;

	returnArray[3] = (byte) temp;

	return returnArray;

    }


    public int getIntFromArray(byte[] intArray)
    {
	int numSoFar;
	byte temp;
	int x = 0xffffff00;
	numSoFar = 0;

	numSoFar =  (intArray[0] & 0xff) | ((intArray[1] << 8) & 0xff00) | ((intArray[2] << 16) & 0xff0000) | ((intArray[3] << 24) & 0xff000000);
	return numSoFar;
    }
    
}
