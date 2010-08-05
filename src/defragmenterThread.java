import java.io.*;
import java.util.*;
import java.sql.Timestamp;

@SuppressWarnings("unchecked")
public class defragmenterThread extends Thread {

	// Packet size variables for this particular execution of the thread
	int packetSize;
	int dataSize;
	int headerSize;
	int FEEDBACK_PACKET_OFFSET = 0;    // arbitrary values
	int FEEDBACK_PACKET_DATA_OFFSET = 5; // arbitrary values
	final int LAST_PACKET_INDICATOR_OFFSET = 1;
	final int PACKET_NUMBER_OFFSET = 0;
	final int LAST_PACKET_INDICATOR_SIZE = 1;
	final int PACKET_NUMBER_SIZE = 1;
	int ipCounter, wirelessFECn, wirelessFECm, ipFECn, ipFECm;
	int totalPackets, totalDataPackets, totalCorruptDataPackets, totalCorrectDataPackets;
	int lastPacket, total_bytes;
	int totalWirelessPackets, totalCorrectedWirelessPackets, totalCorruptWirelessPackets;
	int totalFrames, totalCorruptFrames, totalCorrectFrames;
	Timestamp timestamp;
	boolean wirelessFEC, ipFEC;
	m3Generator feedbackGenerator;
	int nextState[][];
	LinkedList FECbuffer, ipFECBuffer;

	// Size of target packets
	int targetPacketSize = 256;

	// The array that will represent the current packet being defragmented
	byte[] targetPacket;
	byte[] earlyPacket;
	// counter representing where in the target packet the next byte will go
	int targetCounter = 0;
	BufferedWriter outputBuff, perStats, ipGpStats, ferStats, wirelessGpStats, fecStats, fecStats2;
	byte[] currentRadioPacket;
	int oldPacketCounter, prevFeedbackState, totalbytes;
	FileWriter receiver_IP_file, receiver_wireless_file;
	LinkedList feedback_buffer;
	Object feedback_wake;
	byte feedback_num = 0;
	boolean ipFeedback;
	byte linkCondition;
	
	int myPacketCounter = 0;
	int myFrameCounter = 0;

	public defragmenterThread(optionList opt, String receiver_file_wireless, String receiver_file_IP, LinkedList feedbackqueue, Object feedbackwake) {
		try {

			nextState = new int[16][2];
			nextState[0][0] = 0; nextState[0][1] = 1;	nextState[1][0] = 2;	nextState[1][1] = 3;	nextState[2][0] = 4;	nextState[2][1] = 5;
			nextState[3][0] = 6; nextState[3][1] = 7;	nextState[4][0] = 8;	nextState[4][1] = 9;	nextState[5][0] = 10;	nextState[5][1] = 11;
			nextState[6][0] = 12; nextState[6][1] = 13;	nextState[7][0] = 14;	nextState[7][1] = 15;	nextState[8][0] = 0;	nextState[8][1] = 1;
			nextState[9][0] = 2;  nextState[9][1] = 3;	nextState[10][0] = 4;	nextState[10][1] = 5;	nextState[11][0] = 6;   nextState[11][1] = 7;
			nextState[12][0] = 8; nextState[12][1] = 9;	nextState[13][0] = 10;	nextState[13][1] = 11;	nextState[14][0] = 12;	nextState[14][1] = 13;
			nextState[15][0] = 14; nextState[15][1] = 15;
			feedbackGenerator = new m3Generator(opt.lossyMatrix, opt.stateMatrix);
			prevFeedbackState = 0;
			FECbuffer = new LinkedList();
			ipFECBuffer = new LinkedList();
			ipFeedback = opt.ipFeedback;
			feedback_buffer = feedbackqueue;
			feedback_wake = feedbackwake;
			headerSize = opt.headerLength;
			dataSize = opt.dataLength;
			packetSize = dataSize + headerSize;
			targetPacket = new byte[targetPacketSize];
			wirelessFEC = opt.wirelessFEC;
			ipFEC = opt.ipFEC;
			if (wirelessFEC)
			{
				wirelessFECn = opt.wirelessFECn;
				wirelessFECm = opt.wirelessFECm;
			}
			else if (ipFEC) {
				ipFECn = opt.ipFECn;
				ipFECm = opt.ipFECm;
			}
			ipCounter = 0;
			oldPacketCounter = 1;
			totalPackets = 0;
			totalDataPackets = 0;
			totalCorruptDataPackets = 0;
			totalCorrectDataPackets = 0;
			totalFrames = 0;
			totalCorruptFrames = 0;
			totalCorrectFrames = 0;
			totalWirelessPackets = 1;
			totalCorrectedWirelessPackets = 1;
			lastPacket = 1;
			totalbytes = 0;
			total_bytes = 0;
			linkCondition = 0;
			earlyPacket = new byte[targetPacketSize];
			receiver_wireless_file = new FileWriter(receiver_file_wireless);
			receiver_wireless_file.write("\tTimestamp\t\t\tTotal Bytes\tIp Packet Number\t\t\tFrame Number\n");
			receiver_wireless_file.write("\t---------\t\t\t----------\t----------------\t\t\t-------------\n");
			receiver_wireless_file.flush();

			receiver_IP_file = new FileWriter(receiver_file_IP);
			receiver_IP_file.write("\tTimestamp\t\t\tTotal Bytes\tIP Packet Number\n");
			receiver_IP_file.write("\t---------\t\t\t-----------\t-----------------\n");
			receiver_IP_file.flush();

			outputBuff = new BufferedWriter(new FileWriter("outputFile"));
			
			perStats = new BufferedWriter(new FileWriter("E2E_PER_STATISTICS"));
			perStats.write("# Packet Error Rate Statistics\n");
			perStats.write("# format: [timestamp]~~~[packet error rate]~~~[total corrupt packets]~~~[total packets]\n\n");
			perStats.flush();
			
			ipGpStats = new BufferedWriter(new FileWriter("E2E_IP_GP_STATISTICS"));
			ipGpStats.write("# IP-Level Goodput Statistics\n");
			ipGpStats.write("# format: [timestamp]~~~[goodput]~~~[total correct packets]~~~[total packets]\n\n");
			ipGpStats.flush();
			
			ferStats = new BufferedWriter(new FileWriter("E2E_FER_STATISTICS"));
			ferStats.write("# Frame Error Rate Statistics\n");
			ferStats.write("# format: [timestamp]~~~[frame error rate]~~~[total corrupt frames]~~~[total frames]\n\n");
			ferStats.flush();
			
			wirelessGpStats = new BufferedWriter(new FileWriter("E2E_WIRELESS_GP_STATISTICS"));
			wirelessGpStats.write("# Wireless-Level Goodput Statistics\n");
			wirelessGpStats.write("# format: [timestamp]~~~[goodput]~~~[total correct frames]~~~[total frames]\n\n");
			wirelessGpStats.flush();
			
			fecStats = new BufferedWriter(new FileWriter("fecStats"));
			fecStats2 = new BufferedWriter(new FileWriter("fecStats2"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		
		SimonTest.log(SimonTest.defragmenterFlag, "Assembling packet 0...\n");
		
		try {
			byte[] tempArray;
			LinkedList radioPackets;
			byte[] packetNumber = new byte[4];
			int currentPacketCounter;

			while (true) {
				
				synchronized (startup.defrag_wake) {
					//Waiting for queue to fill up

					while (startup.RLE_buffer.size() <= 0) {
						
						startup.defrag_wake.wait();
					}

					/** will append packets from RLE_buffer until we hit a redundant packet.
					 *  Returns false if all packets were not appended. If this is the case,
					 *  we want to wait to get all m redundant packets.
					 */

					if (!flushFECBuffer()) { // fec frames

						//System.out.println("there are FEC packet");

						LinkedList FEC_Group = (LinkedList) FECbuffer.clone();
						FECbuffer.clear();

						// add all redundant packets that have already been put on the RLE_buffer
						for (int i = 0; i < startup.RLE_buffer.size(); i++) {
							tempArray = (byte[]) startup.RLE_buffer.getFirst();
							if (tempArray[startup.FEC_REDUNDANCY_PACKET] == 1)
								FEC_Group.add(startup.RLE_buffer.removeFirst());
						}

						while  (FEC_Group.size() < (wirelessFECm + wirelessFECn)) { // if we have not already received them from the RLE
							startup.defrag_wake.wait();
							FEC_Group.add(startup.RLE_buffer.removeFirst());
						}

						radioPackets = checkFEC(FEC_Group);

						for (int i = 0; i < radioPackets.size(); i++) {
							tempArray = (byte[]) radioPackets.get(i);
							packetNumber[0] = tempArray[4];
							packetNumber[1] = tempArray[5];
							packetNumber[2] = tempArray[6];
							packetNumber[3] = tempArray[7];
							currentPacketCounter = getIntFromArray(packetNumber);
						}

						appendToPacket(radioPackets);
					}


				}
			}
		} 
		catch (Exception e) {
			System.out.println("Error in run method of defragmenter");
			e.printStackTrace();
		}
	}

	/* returns true if there were no FEC packets in RLE_buffer, false if one is found */
	public boolean flushFECBuffer() {
		byte[] packet;
		LinkedList packetsToAppend = new LinkedList();
		byte[] tempArray;
		byte[] packetNumber = new byte[4];
		int currentPacketCounter;

		while (startup.RLE_buffer.size() > 0) {
			packet = (byte[]) startup.RLE_buffer.getFirst();

			if (packet[startup.FEC_REDUNDANCY_PACKET] == 1) {
				/*
				System.out.println("hit FEC packet, Appending what we can");
				for (int i = 0; i < packetsToAppend.size(); i++) {
					    tempArray = (byte[]) packetsToAppend.get(i);
					    packetNumber[0] = tempArray[4];
					    packetNumber[1] = tempArray[5];
					    packetNumber[2] = tempArray[6];
					    packetNumber[3] = tempArray[7];
					    currentPacketCounter = getIntFromArray(packetNumber);
					    System.out.println("packet's frame counter is " + tempArray[8] + " and IP num is " + currentPacketCounter);
				}
				 */
				appendToPacket(packetsToAppend);
				return false;
			}
			totalWirelessPackets++;
			if (packet[startup.PACKET_ERROR_BYTE] == 1)
				totalCorruptWirelessPackets++;
			packet = (byte[]) startup.RLE_buffer.removeFirst();
			FECbuffer.add(packet);

			if (FECbuffer.size() > wirelessFECn) { //we keep only the last n packets. Append the oldest one(s)
				packetsToAppend.add(FECbuffer.removeFirst());
			}

			// if we have reached the final radio packets of the final IP packet, then add them to the
			// tail of the packsToAppend list. This ignores any FEC for the final n packets,
			// but allows termination
			packetNumber[0] = packet[4];
			packetNumber[1] = packet[5];
			packetNumber[2] = packet[6];
			packetNumber[3] = packet[7];
			currentPacketCounter = getIntFromArray(packetNumber);

			if ((currentPacketCounter == (startup.terminationNum - 1)) && packet[LAST_PACKET_INDICATOR_OFFSET] == 1) {
				//System.out.println("hit last packet");
				while (FECbuffer.size() > 0) {
					packetsToAppend.add(FECbuffer.removeFirst());
				}
			}
		}

		//System.out.println("Emptied out whole buffer. Appending same amount from FECbuffer");
		/*
		for (int i = 0; i < packetsToAppend.size(); i++) {
			    tempArray = (byte[]) packetsToAppend.get(i);
			    packetNumber[0] = tempArray[4];
			    packetNumber[1] = tempArray[5];
			    packetNumber[2] = tempArray[6];
			    packetNumber[3] = tempArray[7];
			    currentPacketCounter = getIntFromArray(packetNumber);
			    System.out.println("packet's frame counter is " + tempArray[8] + " and IP num is " + currentPacketCounter + " Error: " + tempArray[startup.PACKET_ERROR_BYTE]);
		}
		 */

		appendToPacket(packetsToAppend);
		return true;
	}

	public LinkedList checkFEC(LinkedList buff) {
		int uncorruptedPacketCounter = 0;
		LinkedList returnList = new LinkedList();
		byte[] tempArray, packetNumber;
		int currentPacketCounter;
		int redundantPacketCount = 0;


		//System.out.println("in checkFEC. wirelessFEC state is " + wirelessFEC);


		for (int i = 0; i < buff.size(); i ++)
		{
			tempArray = (byte[]) buff.get(i);
			packetNumber = new byte[4];
			packetNumber[0] = tempArray[4];
			packetNumber[1] = tempArray[5];
			packetNumber[2] = tempArray[6];
			packetNumber[3] = tempArray[7];
			currentPacketCounter = getIntFromArray(packetNumber);


			//System.out.println("***" + tempArray[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray[startup.PACKET_ERROR_BYTE] + " IP packet number is " + currentPacketCounter);

		}

		if (!wirelessFEC)
		{
			/*
		for (int i = 0; i < buff.size(); i ++)
		    {
			tempArray = (byte[]) buff.get(i);
			System.out.println(tempArray[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray[startup.PACKET_ERROR_BYTE]);
		    }
			 */
			return buff;
		}
		// Check to see the number of redundant packets. If it is 0, we return the list of packets as is
		for (int i = 0; i < buff.size(); i ++)
		{
			tempArray = (byte[]) buff.get(i);
			if (tempArray[startup.FEC_REDUNDANCY_PACKET] == 1)
				redundantPacketCount += 1;
			// Check to see total number of erroneous packets
			if (tempArray[startup.PACKET_ERROR_BYTE] == 0)
				uncorruptedPacketCounter += 1;
			//ak
			System.out.println("# of uncorrupted wireless frames " + uncorruptedPacketCounter);
			//ak
		}
		if (redundantPacketCount != wirelessFECm)
		{
			/*
		for (int i = 0; i < buff.size(); i ++)
		    {
			tempArray = (byte[]) buff.get(i);
			System.out.println(tempArray[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray[startup.PACKET_ERROR_BYTE]);
		    }
			 */
			return buff;
		}



		// Scan to see if the packets are to be sent as is, or if they can be retrieved because FEC (m + n count) is sufficient
		for (int i = 0; i < buff.size(); i ++)
		{
			tempArray = (byte[]) buff.get(i);
			if (tempArray[startup.FEC_REDUNDANCY_PACKET] == 0)
			{
				if (uncorruptedPacketCounter >= wirelessFECn)
				{
					if (tempArray[startup.PACKET_ERROR_BYTE] == 1) {
						totalCorrectedWirelessPackets++;
						//ak
						System.out.println("total corrected wireless frames " + totalCorrectedWirelessPackets);
						//ak
					}
					tempArray[startup.PACKET_ERROR_BYTE] = (byte) 0;
				}
				returnList.add(tempArray);
			}
		}

		for (int i = 0; i < returnList.size(); i ++)
		{
			tempArray = (byte[]) returnList.get(i);
			//System.out.println(tempArray[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray[startup.PACKET_ERROR_BYTE]);
		}

		return returnList;
	}

	public void sendFeedback(){  //ete_feedback
		byte[] feedback_packet = new byte[targetPacketSize];
		feedback_packet[FEEDBACK_PACKET_OFFSET] = feedback_num;
		feedback_num += 1;
		if (feedback_num == 127)
			feedback_num = 0;
		feedback_packet[FEEDBACK_PACKET_DATA_OFFSET] = linkCondition;

		synchronized (startup.E2E_feedback_wake)
		{
			startup.E2E_feedback_buffer.addLast(feedback_packet);
			startup.E2E_feedback_wake.notifyAll();
		}
	}

	/** Method used to collect all the packets so far and remove their headers. Then these packets are combined into a larger packet.
	 * Important - if any radio packet is smaller than its total packet size, that denotes the end of the large packet. in that case it
	 * must be packed off and a new target packet must be created.
	 */

	public void appendToPacket(LinkedList radioPackets) {
		try {
			String x;
			int currentPacketCounter = 0;
			boolean feedbackRequired = false;
			int stateValue, prevLinkCondition;
			byte[] packetNumber = new byte[4];
			byte frameCounter;
			
			while (radioPackets.size() != 0) {
				currentRadioPacket = (byte[]) radioPackets.removeFirst();

				packetNumber[0] = currentRadioPacket[4];
				packetNumber[1] = currentRadioPacket[5];
				packetNumber[2] = currentRadioPacket[6];
				packetNumber[3] = currentRadioPacket[7];
				currentPacketCounter = getIntFromArray(packetNumber);

				// Frame Counter Support
				frameCounter = currentRadioPacket[8];

				if (currentRadioPacket[startup.PACKET_ERROR_BYTE] == 0)
				{
					total_bytes += currentRadioPacket.length - headerSize;
					timestamp = new Timestamp((new Date()).getTime());
					receiver_wireless_file.write("\t" + timestamp + "\t\t" + total_bytes +"\t" +  currentPacketCounter + "\t\t" + frameCounter + "\n");
					receiver_wireless_file.flush();
				}
				if (lastPacket == 1){
					oldPacketCounter = currentPacketCounter;
					earlyPacket = currentRadioPacket;
				}
				lastPacket = (int) currentRadioPacket[LAST_PACKET_INDICATOR_OFFSET];


				if ((targetCounter >= (targetPacketSize - 1)) || (lastPacket == 1) || (currentPacketCounter != oldPacketCounter)){

					if (lastPacket == 1)
					{
						try {
							//System.out.println("BEFORE **** current Radio packet's IP = " + currentPacketCounter + " ,frameCounter is " + frameCounter + " ,targetCounter is " + targetCounter + " ,to copy from radio packet: " + currentRadioPacket.length + " - "  + headerSize + " redundancy is " + targetPacket[startup.FEC_REDUNDANCY_PACKET]);
							System.arraycopy(currentRadioPacket, headerSize, targetPacket, targetCounter, currentRadioPacket.length - headerSize);
							
							SimonTest.log(SimonTest.defragmenterFlag, "Radio frame " + myFrameCounter + ":");
							SimonTest.log(SimonTest.defragmenterFlag, SimonTest.packetToString(currentRadioPacket, headerSize) + "\n");
							myFrameCounter = 0;
							myPacketCounter++;
							SimonTest.log(SimonTest.defragmenterFlag, "Assembling packet " + myPacketCounter + "...\n");
							
							targetCounter =  targetCounter + (currentRadioPacket.length - headerSize);
							oldPacketCounter = currentPacketCounter;

							if (currentRadioPacket[startup.PACKET_ERROR_BYTE] == 1) {
								targetPacket[startup.PACKET_ERROR_BYTE] = 1;
							}
							printFrame(currentRadioPacket);
							
						} catch (Exception e) {
							//System.out.println("AFTER ***current Radio packet's IP = " + currentPacketCounter + " ,frameCounter is " + frameCounter + " ,targetCounter is " + targetCounter + " ,to copy from radio packet: " + currentRadioPacket.length + " - "  + headerSize + " redundancy is " + targetPacket[startup.FEC_REDUNDANCY_PACKET]);
							e.printStackTrace();
						}
					}

					synchronized (startup.listener_wake) {
						if (targetCounter < 40)
						{
							targetPacket = new byte[targetPacketSize];
							// packet being corrupted - cannot change counter if packet is corrupt
							if (currentPacketCounter != 0)
								oldPacketCounter = currentPacketCounter;
							targetCounter = 0;
							continue;
						}

						if (targetCounter < targetPacketSize)
						{
							if (lastPacket == 1)
							{
								targetPacket = fillPacket(targetPacket, targetCounter, currentRadioPacket);
								//targetPacket = trimPacket(targetPacket, targetCounter);
							}
							else
							{
								targetPacket = fillPacket(targetPacket, targetCounter, earlyPacket);
							}
						}

						/**
						 *  IP FEC here
						 */

						ipFECBuffer.add(targetPacket);
						if (ipFEC && targetPacket[startup.FEC_REDUNDANCY_PACKET] == 1) {

							//System.out.println("in here 2 ******************************");
							if (ipFECBuffer.size() < (ipFECn + ipFECm)) {
								//System.out.println("continuing.........");

								targetPacket = new byte[targetPacketSize];
								if (currentPacketCounter != 0)
									oldPacketCounter = currentPacketCounter;
								targetCounter = 0;
								earlyPacket = currentRadioPacket;
								continue;
							}
							else {
								LinkedList ipFECGroup = (LinkedList) ipFECBuffer.clone();
								ipFECBuffer.clear();
								correctAndPrint(ipFECGroup);
							}
						}
						else if (ipFECBuffer.size() > ipFECn) {
							if (currentPacketCounter == 2030) {
								//System.out.println("got here last packet");
							}
							//System.out.println("in here ***********************");
							print((byte[])ipFECBuffer.removeFirst());
						}

						/* If packet is corrupted, then print it out, otherwise ignore it.
			if (targetPacket[startup.PACKET_ERROR_BYTE] == 0)
			    {

				x = new String(targetPacket, 40, targetPacket.length - 40);
				outputBuff.write(x, 0 ,x.length());
				//System.out.print(x);
				startup.listener_buffer.add(targetPacket);
				totalbytes += targetPacket.length - 40;
				try {
				 //Write timestamp into file
				    timestamp = new Timestamp((new Date()).getTime());
				    receiver_IP_file.write("\t" + timestamp + "\t\t" + totalbytes + "\t" + currentPacketCounter + "\n");
				    receiver_IP_file.flush();
				} catch (Exception e) {
				    System.out.println("error in defragmenterThread.java, while writing sender packets to file");
				    e.printStackTrace();
				}
			    }
						 */
						if (ipFeedback)
						{

							// figure out whether feedback is required.
							prevLinkCondition = linkCondition;
							int error = targetPacket[startup.PACKET_ERROR_BYTE];

							/** Now trying out the m3generator to find out the feedback */
							stateValue = feedbackGenerator.getNextState(prevFeedbackState);

							if (stateValue == 0)
								linkCondition = 0;
							else {
								linkCondition = (byte) feedbackGenerator.getLossyFrame(error);
							}
							prevFeedbackState = nextState[prevFeedbackState][stateValue];

							/** Stopped generation of feedback, linkcondition is the desired link condition */
							/**
				// Determine whether feedback is required
				if (prevLinkCondition != linkCondition)
				    feedbackRequired = true;
				else feedbackRequired = false;
							 */
							feedbackRequired = true;
							if (feedbackRequired){
								//System.out.println("Feedback being sent");
								sendFeedback();
							}
						}

						//System.out.println("We are coming in here #5");
						targetPacket = new byte[targetPacketSize];
						if (currentPacketCounter != 0)
							oldPacketCounter = currentPacketCounter;
						targetCounter = 0;
						totalPackets++;
						// if the packet is last packet then do clean up and go-onto the next iteration - cannot do the array copy
						if (lastPacket == 1)
						{
							earlyPacket = currentRadioPacket; // not necessary ... happened at the beginning of while loop
							continue;
						}
					}
				}
				//else {
				// if the above if condition is not satisfied, we are still filling the ipPacket ... if this is a radio packet for a new IP packet
				// then targetPacket has been reset and we fill from the beginning, setting earlyPacket to this new radio packet
				try {

					System.arraycopy(currentRadioPacket, headerSize, targetPacket, targetCounter, currentRadioPacket.length - headerSize);

					SimonTest.log(SimonTest.defragmenterFlag, "Radio frame " + myFrameCounter + ":");
					SimonTest.log(SimonTest.defragmenterFlag, SimonTest.packetToString(currentRadioPacket, headerSize));
					myFrameCounter++;
					
					printFrame(currentRadioPacket);
					
					//if (targetPacket[startup.FEC_REDUNDANCY_PACKET] == 1)
					//System.out.println("copy successful. targetCounter = " + targetCounter + " length = " + currentRadioPacket.length  + " currentIPCounter = " + currentPacketCounter + " frameCounter is " + (int)frameCounter);
				} catch (Exception e)
				{
					//System.out.println("Error!!! -  " + currentRadioPacket[startup.FEC_REDUNDANCY_PACKET] + " targetCounter = " + targetCounter + " length = " + currentRadioPacket.length  + " last packet = " + currentRadioPacket[LAST_PACKET_INDICATOR_OFFSET] + " currentIPCounter = " + currentPacketCounter+ " frameCounter is " + frameCounter);
				}
				if (currentRadioPacket[startup.PACKET_ERROR_BYTE] == 1) {
					if (targetPacket[startup.PACKET_ERROR_BYTE] == 0)
						//System.out.println("corrupting IP packet # " + currentPacketCounter + " and linkCondition is " + ((startup.linkCondition == 1) ? "bad" : "good"));
						targetPacket[startup.PACKET_ERROR_BYTE] = 1;
				}
				earlyPacket = currentRadioPacket;
				targetCounter =  targetCounter + (currentRadioPacket.length - headerSize);
				oldPacketCounter = currentPacketCounter;
				//  }
				//}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Method which removes excess stuff from the end of the target packet */
	public byte[] trimPacket(byte[] target, int end){
		//System.out.println("trimPacket called sir");
		byte[] returnArray = new byte[end];
		System.arraycopy(target, 0, returnArray, 0, end);
		return returnArray;
	}

	/**
	 * Method fills the big packet with the last packet receieved to counter for
	 * lost packets
	 */
	public byte[] fillPacket(byte[] target, int currentLength, byte[] recentPacket){

		//System.out.println("fill packet being called");
		byte[] returnArray = new byte[targetPacketSize];
		System.arraycopy(target, 0 , returnArray, 0, currentLength);
		while (currentLength <= (targetPacketSize - (recentPacket.length - headerSize)))
		{
			System.arraycopy(recentPacket, headerSize, returnArray, currentLength, recentPacket.length - headerSize);
			currentLength += (recentPacket.length - headerSize);

			//System.out.println("recentPacket.length = " + recentPacket.length + "\n currentLength = " + currentLength); 
		}
		//returnArray = trimPacket(returnArray, currentLength);
		if (currentLength < targetPacketSize) 
		{
			System.arraycopy(recentPacket, headerSize, returnArray, currentLength, (targetPacketSize - currentLength));
		}


		return returnArray;
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

	private void correctAndPrint(LinkedList buff) {
		int uncorruptedPacketCounter = 0;
		byte[] tempArray;
		int redundantPacketCount = 0;
		byte[] tempArray2, packetNumber;
		int currentPacketCounter;

		/*
	for (int i = 0; i < buff.size(); i ++)
	    {
		tempArray2 = (byte[]) buff.get(i);
		packetNumber = new byte[4];
		packetNumber[0] = tempArray2[4];
		packetNumber[1] = tempArray2[5];
		packetNumber[2] = tempArray2[6];
		packetNumber[3] = tempArray2[7];
		currentPacketCounter = getIntFromArray(packetNumber);

		System.out.println("***" + tempArray2[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray2[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray2[startup.PACKET_ERROR_BYTE] + " IP packet number is " + currentPacketCounter);
	    }
		 */
		for (int i = 0; i < buff.size(); i ++)
		{
			tempArray = (byte[]) buff.get(i);
			if (tempArray[startup.FEC_REDUNDANCY_PACKET] == 1)
				redundantPacketCount += 1;
			// Check to see total number of erroneous packets
			if (tempArray[startup.PACKET_ERROR_BYTE] == 0)
				uncorruptedPacketCounter += 1;
		}
		if (redundantPacketCount != ipFECm)
		{
			return;
		}

		// Scan to see if the packets are to be sent as is, or if they can be retrieved because FEC (m + n count) is sufficient
		for (int i = 0; i < buff.size(); i ++)
		{
			tempArray = (byte[]) buff.get(i);
			if (tempArray[startup.FEC_REDUNDANCY_PACKET] == 0)
			{
				if (uncorruptedPacketCounter >= ipFECn)
				{
					tempArray[startup.PACKET_ERROR_BYTE] = (byte) 0;
				}
				print(tempArray);
			}
		}
		/*
		for (int i = 0; i < buff.size(); i ++)
		    {
			tempArray2 = (byte[]) buff.get(i);
			packetNumber = new byte[4];
			packetNumber[0] = tempArray2[4];
			packetNumber[1] = tempArray2[5];
			packetNumber[2] = tempArray2[6];
			packetNumber[3] = tempArray2[7];
			currentPacketCounter = getIntFromArray(packetNumber);

			System.out.println("***" + tempArray2[startup.FEC_REDUNDANCY_PACKET] + "(" + tempArray2[startup.FEC_ID_BYTE] + ")" + ": Error =" + tempArray2[startup.PACKET_ERROR_BYTE] + " IP packet number is " + currentPacketCounter);
		    }
		 */

	}

	private void print(byte[] packet) {
		totalDataPackets++;
		String x;

		byte[] packetNumber = new byte[4];
		packetNumber[0] = packet[4];
		packetNumber[1] = packet[5];
		packetNumber[2] = packet[6];
		packetNumber[3] = packet[7];
		int currentPacketCounter = getIntFromArray(packetNumber);

		/** Write timestamp into file */
		timestamp = new Timestamp((new Date()).getTime());		    
		try {

			if (packet[startup.PACKET_ERROR_BYTE] == 0)
			{
				totalCorrectDataPackets++;
				x = new String(packet, 40, packet.length - 40);
				outputBuff.write(x, 0 ,x.length());
				outputBuff.flush();
				//System.out.print(x);
				startup.listener_buffer.add(packet);
				totalbytes += packet.length - 40;

				receiver_IP_file.write("\t" + timestamp + "\t\t" + totalbytes + "\t" + currentPacketCounter + "\n");
				receiver_IP_file.flush();

				//  float goodput = (float)totalCorrectDataPackets / totalPackets;
				//goodputStats.write("\t" + timestamp + "\t\t" + goodput + "\t" + totalCorrectDataPackets + "\t" + totalPackets + "\n");
				//goodputStats.flush();

			}
			else {
				totalCorruptDataPackets++;
				//float per = (float)totalCorruptDataPackets/totalDataPackets;
				//perStats.write("\t" + timestamp + "\t\t" + per + "\t" + totalCorruptDataPackets + "\t" + totalDataPackets + "\n");
				//perStats.flush();
			}
			float goodput = (float) totalCorrectDataPackets / totalDataPackets;
			ipGpStats.write(timestamp + "~~~" + goodput + "~~~" + totalCorrectDataPackets + "~~~" + totalDataPackets + "\n");
			ipGpStats.flush();

			float per = (float) totalCorruptDataPackets / totalDataPackets;
			perStats.write(timestamp + "~~~" + per + "~~~" + totalCorruptDataPackets + "~~~" + totalDataPackets + "\n");
			perStats.flush();


			//ak
			float corrected = (float)totalCorrectedWirelessPackets/totalWirelessPackets;

			fecStats.write("\t" + timestamp + "\t\t" + corrected + "\t" + totalCorrectedWirelessPackets + "\t" + totalWirelessPackets + "\n");
			fecStats.flush();

			float corruption = (float)totalCorruptWirelessPackets/totalWirelessPackets;
			fecStats2.write("\t" + timestamp + "\t\t" + corruption + "\t" + totalCorruptWirelessPackets + "\t" + totalWirelessPackets + "\n");
			fecStats2.flush();
			//ak

			float correction = (float)totalCorrectedWirelessPackets/totalCorruptWirelessPackets;

			fecStats.write("\t" + timestamp + "\t\t" + correction + "\t" + totalCorrectedWirelessPackets + "\t" + totalCorruptWirelessPackets + "\n");
			fecStats.flush();

			//termination
			if (startup.termination)
			{
				//System.out.println("in termination sequence");
				if ((currentPacketCounter + 1) == startup.terminationNum) {
					//terminator window = new terminator();
					//window.show();
					System.out.println("terminating");
					startup.stop();
				}
			}
			//System.out.println("numdataPackets is " + totalDataPackets + " and current packetnum is " + currentPacketCounter);
		} catch (Exception e) {
			System.out.println("error in defragmenterThread.java, while writing sender packets to file");
			e.printStackTrace();
		}
	}
	
	public void printFrame(byte[] frame) {
		if (frame[startup.PACKET_ERROR_BYTE] == 1) {
			totalCorruptFrames++;
		}
		else {
			totalCorrectFrames++;
		}
		totalFrames++;
		
		float fer = (float) totalCorruptFrames / totalFrames;
		float goodput = (float) totalCorrectFrames / totalFrames;
		Timestamp ts = new Timestamp((new Date()).getTime());
		try {
			ferStats.write(ts + "~~~" + fer + "~~~" + totalCorruptFrames + "~~~" + totalFrames + "\n");
			ferStats.flush();
			
			wirelessGpStats.write(ts + "~~~" + goodput + "~~~" + totalCorrectFrames + "~~~" + totalFrames + "\n");
			wirelessGpStats.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
