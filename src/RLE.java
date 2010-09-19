import java.io.*;
import java.util.*;

public class RLE extends Thread {

	final int IP_HEADER_SIZE = 40;
	final int IP_DATA_SIZE = 216;
	int nextState[][];
	String networkModel;
	boolean error;                 // is true if error model is chosen, else it is false
	double[][] lossyMatrix, stateMatrix;
	int changeOfState;
	double FER;
	errorModel lossGenerator;
	int goodCount, badCount;
	byte linkCondition = 0;
	java.util.Date timer;
	int packetSize, headerSize, dataSize;
	byte feedbackNumber = 0;
	m3Generator feedbackGenerator;
	boolean wirelessFeedback, wirelessLosses;
	final int FEEDBACK_PACKET_DATA_OFFSET = 5;
	final int FEEDBACK_PACKET_OFFSET = 0;
	int prevFeedbackState, burstCounter, errorCounter;

	//ak
	BufferedWriter burstStats, ferStats;
	int NumErrors;
	//ak

	int currentState = 0;

	//ak
	int TotalNumFrames = 0;
	//ak


	public RLE(optionList opt){

		nextState = new int[16][2];
		nextState[0][0] = 0;	nextState[0][1] = 1;	nextState[1][0] = 2;	nextState[1][1] = 3;	nextState[2][0] = 4;	nextState[2][1] = 5;
		nextState[3][0] = 6;	nextState[3][1] = 7;	nextState[4][0] = 8;	nextState[4][1] = 9;	nextState[5][0] = 10;	nextState[5][1] = 11;
		nextState[6][0] = 12;	nextState[6][1] = 13;	nextState[7][0] = 14;	nextState[7][1] = 15;	nextState[8][0] = 0;	nextState[8][1] = 1;
		nextState[9][0] = 2;	nextState[9][1] = 3;	nextState[10][0] = 4;	nextState[10][1] = 5;	nextState[11][0] = 6;   nextState[11][1] = 7;
		nextState[12][0] = 8;   nextState[12][1] = 9;	nextState[13][0] = 10;	nextState[13][1] = 11;	nextState[14][0] = 12;	nextState[14][1] = 13;
		nextState[15][0] = 14;	nextState[15][1] = 15;

		headerSize = opt.headerLength;
		dataSize = opt.dataLength;
		//ak
		packetSize = headerSize + dataSize;
		//ak
		feedbackGenerator = new m3Generator(opt.lossyMatrix, opt.stateMatrix);
		prevFeedbackState = 0;
		networkModel = opt.wirelessErrorModel;
		linkCondition = 0;
		wirelessLosses  = opt.wirelessModel;
		lossyMatrix = opt.lossyMatrix;
		stateMatrix = opt.stateMatrix;
		changeOfState = opt.changeOfState;
		FER = opt.FER;

		goodCount = 0;
		badCount = 0;

		if (opt.wirelessModel)
		{

			if (opt.wirelessMode.equals("ERROR"))
				error = true;
			else error = false;

			wirelessFeedback = opt.wirelessFeedback;

			if (opt.wirelessErrorModel.equals("HMM"))
				lossGenerator = new hmmGenerator(opt.FER, opt.stateMatrix);
			else if (opt.wirelessErrorModel.equals("M3"))
				lossGenerator = new m3Generator(opt.lossyMatrix, opt.stateMatrix);
			else if (opt.wirelessErrorModel.equals("GILBERT"))
				lossGenerator = new gilbertGenerator(opt.lossyMatrix);
			else if (opt.wirelessErrorModel.equals("MTA"))
				lossGenerator = new mtaGenerator();
		}
		try {
			burstStats = new BufferedWriter(new FileWriter("burstStats"));

			//ak
			ferStats = new BufferedWriter(new FileWriter("WIRELESS_FER_STATISTICS"));
			//ak

			burstCounter = 1;
			errorCounter = 0;
			//ak
			NumErrors=0;
			//ak
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(){
		
		try {
			byte[] originalPacket;           // Packet picked up from the fragmented queue
			LinkedList radioPackets;
			byte[] modifiedPacket;           // Packet returned by the RLE after modification

			while (true) {

				//get radio frames fom the fragmented queue

				synchronized (startup.RLE_wake) {

					while (startup.fragmented_buffer.size() == 0) {
						
						startup.RLE_wake.wait();
					}

					radioPackets = (LinkedList) startup.fragmented_buffer.clone();
					startup.fragmented_buffer.clear();
				}

				// Modify radio frames and then send them one by one on the RLE_buffer.
				System.out.println("radioPackets.size \t" + radioPackets.size()) ;

				while (radioPackets.size() != 0) {

					originalPacket = (byte[]) radioPackets.removeFirst();
					//System.out.println(originalPacket[startup.FEC_REDUNDANCY_PACKET]+ "(" + originalPacket[startup.FEC_ID_BYTE] + ")");


					System.out.println("wirelessfeedback= \t" + wirelessFeedback + "wirelessLosses = \t" + wirelessLosses);


					if (wirelessLosses)
						modifiedPacket = messUpPacket(originalPacket);
					else modifiedPacket = originalPacket;

					if (modifiedPacket != null)
					{
						synchronized (startup.defrag_wake) {
							startup.RLE_buffer.add(modifiedPacket);
							startup.defrag_wake.notifyAll();
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public byte[] messUpPacket(byte[] origPacket){

		int x;
		int feedback_quality, oldStateValue, prevLinkCondition;
		boolean feedbackRequired = false;

		x = lossGenerator.generate();

		if (wirelessFeedback)
		{

			System.out.println("here, sending feedback");
			oldStateValue = currentState;
			/* We will get the information on the state of the link from
		   the loss generator, since it is determining the losses of
		   each individual frame. Later, we will use feedback generator
		   to determine whether feedback packets are given loss as well.
			 */
			currentState = lossGenerator.getCurrentState();
			/*
		prevLinkCondition = linkCondition;
		Now trying out the m3generator to find out the feedback

		stateValue = feedbackGenerator.getNextState(prevFeedbackState);

		if (stateValue == 0)
		    linkCondition = 0;
		else {
		    linkCondition = (byte) feedbackGenerator.getLossyFrame(x);
		    //prevFeebackLossy = nextState[x][linkCondition];
		}
			 */


			/** Stopped generation of feedback, linkcondition is the desired link condition */

			// Determine whether feedback is required
			//feedbackRequired = true;
			try {
				//  if (linkCondition == x) {
				//  			burstStats.write("TRUE\n");
				//  			burstStats.flush();
				//  		    }
				//  		    else {
				//  			burstStats.write("FALSE\n");
				//  			burstStats.flush();
				//  		    }

				if (oldStateValue == 0 && currentState == 1) {
					feedbackRequired = true; // needs to send feedback
					burstStats.write(burstCounter + "\t" + errorCounter + "\n");
					burstStats.write("LOSSY\t");
					burstCounter = 1;
					errorCounter = 0;
					burstStats.flush();
				}
				else if (oldStateValue == 1 && currentState == 0) {
					feedbackRequired = true;
					burstStats.write(burstCounter + "\t" + errorCounter + "\n");
					burstStats.write("GOOD\t");
					burstCounter = 1;
					errorCounter = 0;
					burstStats.flush();
				}
				else burstCounter++;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Removed for now while no error for feedback packets NP 3/13
			//prevFeedbackState = nextState[prevFeedbackState][stateValue];
		}

		// see if feedBack needs to be sent
		if (feedbackRequired && wirelessFeedback) {


			sendFeedback();
		}


		// if the model for losses is delay then just don't send the packet
		if (!(error) && (x == 1))
		{
			return null;
		}
		else if (!(error))
		{
			return origPacket;
		}

		// if the model for losses is error, then mess up the packet
		if (x == 1) {
			origPacket[startup.PACKET_ERROR_BYTE] = (byte) 1;
			errorCounter++;
			//ak
			NumErrors++;
			//ak  System.out.println("errorcounter " + errorCounter);

		}
		else origPacket[startup.PACKET_ERROR_BYTE] = (byte) 0;

		//ak
		TotalNumFrames++;
		//System.out.println("total num frames " + TotalNumFrames);

		float FrameErrorRate = (float) NumErrors / TotalNumFrames;

		try {
			ferStats.write(NumErrors + "\t" + TotalNumFrames + "\t" + FrameErrorRate + "\n");
		} catch (Exception s) {
			System.out.println("error in writing ferStats");

		}

		//System.out.println("FER: " + FrameErrorRate);
		//ak

		return origPacket;
	}


	// ak rewrite sendFeedback
	//BS sends feedback frames to sender
	public void sendFeedback() {
		//String packet;
		//byte[] feedback_packet;

		byte[] feedback_frame;

		//byte[] stringPacket;

		feedback_frame = new byte[packetSize];

		//feedbackNum
		feedback_frame[FEEDBACK_PACKET_OFFSET]= feedbackNumber;
		feedbackNumber ++;
		if (feedbackNumber == 127)
			feedbackNumber = 0;

		//linkcondition
		feedback_frame[FEEDBACK_PACKET_DATA_OFFSET] = (byte) currentState;

		//send feedback frame through the wireless link
		feedback_frame = messUpPacket(feedback_frame);

		if (feedback_frame != null)
		{
			synchronized (startup.feedback_frame_wake) {
				startup.feedback_frame_buffer.add(feedback_frame);
				startup.feedback_frame_wake.notifyAll();
			}
		}

	}
}






