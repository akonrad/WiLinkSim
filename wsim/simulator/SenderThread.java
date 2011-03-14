package simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The SenderThread class takes an input file, packetizes it and puts it into the packet buffer.
 * 
 * @author 	Kat Villariba
 * @version 1.0 2011-MAR-13
 */
public class SenderThread extends Thread {
	
	private Buffer<IPPacket>	packetBuffer;
	private File				inputFile;
	
	// Constructor
	
	public SenderThread(Buffer<IPPacket> i_packetBuffer, File i_inputFile) {
		packetBuffer	= i_packetBuffer;
		inputFile		= i_inputFile;
	}

	// Methods
	
	/**
	 * Packetizes input file and puts packets into packet buffer
	 */
	public void run() {
		int				numBytesRead	= 0;
		int 			dataLength 		= 216;
		byte[]  		data 			= new byte[dataLength];
		FileInputStream	fileStream 		= null;
		
		try {
			fileStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			// Read until max data length or end of file			
			while (fileStream.read(data) != -1) {
				int id = numBytesRead;
				
				// Create packet
				IPPacket packet = new IPPacket(id, data);
							
				// Put packet in packet buffer
				packetBuffer.put(packet);
				
				// Increment number of bytes read
				numBytesRead += data.length;
				
				// Sleep for a randomly chosen time
				try {
	    			Thread.sleep( (int) Math.random() * 100);
	    		} catch (InterruptedException e) { }				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}