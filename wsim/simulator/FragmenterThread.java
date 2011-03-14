package simulator;

/**
 * The FragmenterThread class takes packets from the IP packet buffer, breaks up the packets into
 * radio frames and puts the radio frames into the radio packet buffer.
 * 
 * @author 	Kat Villariba
 * @version 1.0 2011-MAR-13
 */
public class FragmenterThread extends Thread {

	private Buffer<IPPacket> packetBuffer;
	
	// Constructor
	
	public FragmenterThread(Buffer<IPPacket> i_packetBuffer) {
		packetBuffer = i_packetBuffer;
	}
	
	// Methods
	
	public void run() {
		while (true) {
			// Get packet from packet buffer
			IPPacket packet = packetBuffer.get();

			// Sleep for a randomly chosen time
			try {
    			Thread.sleep( (int) Math.random() * 100);
    		} catch (InterruptedException e) { }				

		}
	}
}
