package simulator;

import java.io.File;

public class Simulator {

	public static void main(String[] args) {
		// Input file
		
		File inputFile = new File("/home/kat/Repositories/WiLinkSim/wsim/simulator/test.txt");
		
		// Buffers
		
		Buffer<IPPacket>	packetBuffer	= new Buffer<IPPacket>();
		
		// Threads
		
		SenderThread		sender			= new SenderThread(packetBuffer, inputFile);
		FragmenterThread	fragmenter		= new FragmenterThread(packetBuffer);
		
		// Start threads
		
		sender.start();
		fragmenter.start();
		
		try {
			sender.join();
			fragmenter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}