import java.io.*;
import java.util.*;
import java.sql.Timestamp;

public class fragmenterThread extends Thread { 

	boolean feedback, wirelessFEC;
	Random rand;
	Timestamp timer;
	FileWriter wirelessDump, wirelessFeedback;
	LinkedList unfrag_queue, frag_queue;
	Object unfrag_queue_lock, frag_queue_lock;
	int ipPacketNumber, totalbytes;
	int packetSize,dataSize, headerSize, totalPackets;
	int linkCondition = 0;
	final int LAST_PACKET_INDICATOR_OFFSET = 1;
	final int PACKET_NUMBER_OFFSET = 0;
	final int LAST_PACKET_INDICATOR_SIZE = 1;
	final int PACKET_NUMBER_SIZE = 1;
	final int FEEDBACK_PACKET_DATA_OFFSET = 5;
	final int FEEDBACK_PACKET_OFFSET = 0;
	int FECid = 0;
	int radioPacketCounter = -1;   // Made to start with negative value due to modulo calculation - need 0 to be first value after increase
	int ipCounter, wirelessFECn, wirelessFECm;
	LinkedList feedback_queue;
	Object feedback_lock;
	int prevLinkCondition = 0;
	//boolean firstTime = true;

	/**
	 * Fragmenter
	 */

	public fragmenterThread(LinkedList queueFrom, Object queueFromLock,LinkedList queueTo, Object queueToLock, optionList opt, String sender_dump, String feedback_dump, LinkedList feedbackqueue, Object feedbacklock)
	{
		try 
		{
			rand = new Random();
			wirelessDump = new FileWriter(sender_dump);
			wirelessDump.write("Timestamp\t\t\tTotal Bytes\tIP Packet number\n");
			wirelessDump.write("---------\t\t\t-----------\t----------------\n");

			feedback = opt.wirelessFeedback;
			if (feedback){
				wirelessFeedback = new FileWriter(feedback_dump);
				wirelessFeedback.write("Timestamp\t\t\tFeedback\tPacket number\n");
				wirelessFeedback.write("---------\t\t\t--------\t----------------\n");
			}
			unfrag_queue = queueFrom;
			unfrag_queue_lock = queueFromLock;
			frag_queue = queueTo;
			unfrag_queue_lock = queueToLock;
			headerSize = opt.headerLength;
			dataSize = opt.dataLength;
			packetSize = headerSize + dataSize;
			feedback_queue = feedbackqueue;
			feedback_lock = feedbacklock;
			totalbytes = 0;
			totalPackets = 0;
			wirelessFEC = opt.wirelessFEC;
			if (wirelessFEC)
			{		
				wirelessFECn = opt.wirelessFECn;
				wirelessFECm = opt.wirelessFECm;
			}		    
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main run method for this thread
	 */
	public void run()
	{

		try
		{
			LinkedList ipPacketList;
			LinkedList brokenPackets;
			byte[] ipPacket;             // The original IP packet received by the fragmenter
			byte[] feedback;
			LinkedList clone;
			byte feedbackNum;
			String tempCondition;

			int debugPacketCounter = 0;

			while (true) {
				// 1) Pick up the IP packets waiting to be fragmented on the queue from the sender

				synchronized (startup.frag_wake) {

					while (startup.packet_buffer.size() == 0) 	{

						startup.frag_wake.wait();
					}

					// 2) Get a copy of all the packets on the IP queue for own use, and release the lock

					ipPacketList = (LinkedList) startup.packet_buffer.clone();
					startup.packet_buffer.clear();

				}

				// 3) Break all the IP packets just obtained into radio packets

				while (ipPacketList.size() != 0) {
					ipPacket = (byte[]) ipPacketList.removeFirst();
					brokenPackets = breakPackets(ipPacket);

					// 4) Send the just constructed radio packets to the Radio Base Station.
					// The FEC will be added here to each packet before it is sent

					SimonTest.log(SimonTest.fragmenterFlag, "Breaking up packet " + debugPacketCounter + " into radio frames...\n");
					int frameCounter = 0;
					for(Object x : brokenPackets) {
						SimonTest.log(SimonTest.fragmenterFlag, "Radio Frame " + frameCounter + ":");
						SimonTest.log(SimonTest.fragmenterFlag, SimonTest.packetToString((byte []) x, headerSize));
						frameCounter++;
					}
					SimonTest.log(SimonTest.fragmenterFlag, "\n");
					debugPacketCounter++;

					sendPackets(brokenPackets);

					//if (firstTime)
					//	    firstTime = false;
				}

				//ak, read feedback_frames

				synchronized (startup.feedback_frame_wake) {

					while (startup.feedback_frame_buffer.size() != 0) {
						System.out.println("there are feedback frames");

						clone = (LinkedList) startup.feedback_frame_buffer.clone();
						startup.feedback_frame_buffer.clear();

						while (clone.size() != 0) { //we want the last feedback value
							feedback = (byte[]) clone.removeFirst();
							prevLinkCondition = linkCondition;
							linkCondition = (int) feedback[FEEDBACK_PACKET_DATA_OFFSET];
							feedbackNum = feedback[FEEDBACK_PACKET_OFFSET];


							if (feedback[startup.PACKET_ERROR_BYTE] == 0) {
								// write into the file

								timer = new Timestamp((new Date()).getTime());

								//get new value for linkcondition

								if ((linkCondition == 0) && (prevLinkCondition == 1)) {
									tempCondition = "GOOD";
									wirelessFEC = false;
									wirelessFeedback.write(timer + "\t" + feedbackNum + "\t" + tempCondition + "\n");
									startup.linkCondition = 0;
								}
								else if ((linkCondition == 1) && (prevLinkCondition == 0)) {
									tempCondition = "BAD";
									wirelessFEC = true;
									wirelessFeedback.write(timer + "\t" + feedbackNum + "\t" + tempCondition + "\n");
									startup.linkCondition = 1;
								}
							}
						}
					}
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public LinkedList breakPackets(byte[] inputPacket){
		LinkedList radioPacketCollection = new LinkedList();
		byte[] radioPacket = new byte[packetSize];
		byte[] lastPacket;
		byte[] packetNumber = new byte[4];
		byte[] packetNumber2 = new byte[4];
		int pppCounter = 0;
		int radioCounter = 0;      // radio counter shows how much of the packet has been filled.
		byte frameCounter = 0;
		String x;

		//ipCounter = inputPacket[0];
		packetNumber2[0] = inputPacket[4];
		packetNumber2[1] = inputPacket[5];
		packetNumber2[2] = inputPacket[6];
		packetNumber2[3] = inputPacket[7];
		ipCounter = getIntFromArray(packetNumber2);

		while (pppCounter < inputPacket.length) {

			if (radioCounter == 0) {        // The radio packet is empty and header must be filled

				// 1) Fill the header with random ones and zeroes

				for (int i = 0; i < headerSize; i ++){
					if (rand.nextBoolean()) {
						radioPacket[i] = 1;
					}
					else radioPacket[i] = 0;
				}
				radioCounter = headerSize;

				packetNumber = getByteArray(ipCounter);
				//radioPacket[PACKET_NUMBER_OFFSET] =  ipCounter;
				radioPacket[4] = packetNumber[0];
				radioPacket[5] = packetNumber[1];
				radioPacket[6] = packetNumber[2];
				radioPacket[7] = packetNumber[3];

				// Frame Counter Support
				radioPacket[8] = frameCounter; // for a given IP packet, what part of it is represented by this radio packet

				// Frame Counter support

				frameCounter += 1;
				if (frameCounter == 127) {
					//System.out.println("frame counter reseted. Current value is " + frameCounter);
					frameCounter = 0;
				}
				// initially set all radio packet last packet field to be 0, later change it for last packet

				radioPacket[LAST_PACKET_INDICATOR_OFFSET] = (byte) 0;
				radioPacket[startup.PACKET_ERROR_BYTE] = (byte) 0;

			}

			// header has been filled and we are in the process of filling the data segment of the radio packet
			if (radioCounter < packetSize){
				radioPacket[radioCounter] = inputPacket[pppCounter];
				radioCounter++;
				pppCounter++;
			}
			else {          // the radio packet has been filled
				radioPacketCollection.add(radioPacket); // all the radio packets created to represent the IP packet
				//x = new String(radioPacket, headerSize, dataSize);
				//System.out.print(x);

				radioPacket = new byte[packetSize]; //make a new one for the next iteration of the loop
				//totalbytes += dataSize;
				radioCounter = 0;
			}
		}

		// IP packet is over and last radio packet may or may not be full
		if (ipCounter == 2030) {
			//System.out.println("last IP packet over, about to set up last packet");
		}
		if (radioCounter != 0) {
			lastPacket = new byte[radioCounter];
			//totalbytes += radioCounter - headerSize;
			System.arraycopy(radioPacket, 0, lastPacket, 0, radioCounter);
			// since this is the last fragment of the ipPacket, set its last packet size to 1
			lastPacket[LAST_PACKET_INDICATOR_OFFSET] = (byte) 1;
			totalPackets += 1;
			radioPacketCollection.add(lastPacket);
			if (ipCounter == 2030) {
				//System.out.println("last radio packet added, flag set to " + lastPacket[LAST_PACKET_INDICATOR_OFFSET] + " and frame is " + lastPacket[8]);
			}


			//System.out.println(" ********  size of last packet is " + radioCounter + " normal packet size is " + packetSize + " last's frame count is " + lastPacket[8] + " IP number is " + ipCounter);
			//x = new String(lastPacket, headerSize, radioCounter - headerSize);
			//System.out.println(x);


			// resetting the counter to 0 once it passes 255
			// assuming that packets don't take so long to arrive that 255 have passed btween them

			//if (ipCounter == 127)
			//ipCounter = 0;

		}



		radioPacket = null;
		lastPacket = null;
		return radioPacketCollection;
	}



	// sendPackets - takes in a linkedList of radio packets and sends them to the RLE

	public void sendPackets(LinkedList radioPackets) {

		byte[] radioPacket;

		while (radioPackets.size() != 0){

			//System.out.println("wirelessFEC" + wirelessFEC + "linkCondition" + linkCondition);

			if (wirelessFEC && linkCondition == 1) //we are in a bad state
			{
				//put n radio frames  and m rendundant frames into fragmenter_buffer
				//send n frames
				for (int i = 0; i < wirelessFECn; i++ ) {

					//  System.out.println("sending a frame n = \t" + i);
					radioPacket = (byte[]) radioPackets.removeFirst();
					sendFrame(radioPacket);

				}

				//sends m redundant frames
				addFEC();
				
			} else { //only put 1 frame w/out FEC

				radioPacket = (byte[]) radioPackets.removeFirst();
				sendFrame(radioPacket);
				
			}
		}
	}

	public void sendFrame(byte[] radioPacket) {
		int tempIPCounter = 0;
		byte[] packetNumber = new byte[4];
		byte frameCounter = 0;

		try {
			//tempIPCounter = radioPacket[PACKET_NUMBER_OFFSET];
			packetNumber[0] = radioPacket[4];
			packetNumber[1] = radioPacket[5];
			packetNumber[2] = radioPacket[6];
			packetNumber[3] = radioPacket[7];

			//uses 4 bytes for the IP packet seq#
			// the IP packet Seq# that this radio packet belongs to
			tempIPCounter = getIntFromArray(packetNumber);

			// Frame Counter Support, uses one byte for the radio frame seq#
			frameCounter = radioPacket[8];

			timer = new Timestamp((new Date()).getTime());
			//System.out.print(new String(radioPacket, 5, radioPacket.length - 5));
			wirelessDump.write(timer + "\t\t" + totalbytes + "\t\t" + tempIPCounter + "\t\t" + frameCounter +"\n");
			wirelessDump.flush();
			totalbytes += radioPacket.length - headerSize; // total number of bytes sent so far
		} catch (Exception e){
			e.printStackTrace();
		}

		synchronized (startup.RLE_wake) {

			if (wirelessFEC && linkCondition == 1)  {
				radioPacket[startup.FEC_ID_BYTE] = (byte) FECid;
			} else {
				radioPacket[startup.FEC_ID_BYTE] = (byte) -1; // a non FEC radio frame
			}

			radioPacket[startup.FEC_REDUNDANCY_PACKET] = (byte) 0;
			radioPacketCounter += 1;
			
			startup.fragmented_buffer.add(radioPacket);
			startup.RLE_wake.notifyAll();
		}
	}

	public void addFEC(){  //send m redundant frames every n radio data frames
		byte[] FECpacket;

		// radioPacketCounter tells how many radio data frames have been put in fragmented_buffer

		System.out.println("num of fr in buffer=" + radioPacketCounter +"m=" + wirelessFECm + "\t n= " + wirelessFECn);

		if (radioPacketCounter % (wirelessFECn) == (wirelessFECn - 1))
		{
			for (int i = 0; i < wirelessFECm; i ++)  //sends m rendundant frames
			{
				FECpacket = new byte[packetSize];
				FECpacket[startup.PACKET_ERROR_BYTE] = (byte) 0;
				FECpacket[startup.FEC_REDUNDANCY_PACKET] = (byte) 1;
				FECpacket[startup.FEC_ID_BYTE] = (byte) FECid;

				//System.out.println("sending FEC packet " + FECid);
				
				synchronized (startup.RLE_wake) {
					startup.fragmented_buffer.add(FECpacket);
					startup.RLE_wake.notifyAll();
				}
			}
			FECid += 1;
			if (FECid > 127)
				FECid = 0;
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