import java.io.*;
import java.util.*;

public class startup {

    // Static variables used by the whole package

    public static final int PACKET_ERROR_BYTE = 10; // if this bit is 1, then the packet is corrupted.
    public static final int FEC_ID_BYTE = 11;
    public static final int FEC_REDUNDANCY_PACKET = 12;
    public static LinkedList packet_buffer;

    public static LinkedList feedback_buffer, feedback_frame_buffer, RLE_buffer, feedback_unfragmented_buffer, feedback_fragmented_buffer, feedback_defragmented_buffer;

    public static boolean packet_ready;

    public static Object frag_wake, RLE_wake, defrag_wake, listener_wake, feedback_wake, defrag2_wake, feedback_unfragmented_wake, feedback_frame_wake, feedback_fragmented_wake, feedback_defragmented_wake, E2E_feedback_wake;

    public static LinkedList fragmented_buffer, defrag_buffer, listener_buffer, E2E_feedback_buffer;

    public static boolean termination = false;
    public static int terminationNum = 0;
    public static int linkCondition = 0;

    public static senderThread sender;
    public static fragmenterThread fragmenter;

    public static RLE rle;

    public static defragmenterThread defrag;

    public static void go(String data_file) {

		optionList opt = new optionList("options");
		//opt.print();
	
		packet_buffer = new LinkedList();
		feedback_buffer = new LinkedList();
		feedback_frame_buffer = new LinkedList();
		fragmented_buffer = new LinkedList();
		E2E_feedback_buffer = new LinkedList();
		E2E_feedback_wake = new Object();
		feedback_frame_wake = new Object();
		feedback_fragmented_buffer = new LinkedList();
		feedback_defragmented_buffer = new LinkedList();
		feedback_unfragmented_buffer = new LinkedList();
		feedback_unfragmented_wake = new Object();
		feedback_defragmented_wake = new Object();
		feedback_fragmented_wake = new Object();
		RLE_buffer = new LinkedList();
		defrag_buffer = new LinkedList();
		listener_buffer = new LinkedList();
		frag_wake = new Object();
		RLE_wake = new Object();
		defrag2_wake = new Object();
		feedback_wake = new Object();
		packet_ready = false;
		defrag_wake = new Object();
		listener_wake = new Object();
	
	
		sender = new senderThread(data_file,opt,packet_buffer, frag_wake, E2E_feedback_buffer, E2E_feedback_wake ,"SENDER_IP_DUMP.txt", "E2E_FEEDBACK_DUMP.txt");
		fragmenter = new fragmenterThread(packet_buffer, frag_wake, fragmented_buffer, RLE_wake, opt, "SENDER_WIRELESS_DUMP.txt", "WIRELESS_FEEDBACK_DUMP.txt", feedback_fragmented_buffer, feedback_fragmented_wake);
	
		rle = new RLE(opt);
		sender.start();
		fragmenter.start();
		rle.start();
	
		defrag = new defragmenterThread(opt, "RECEIVER_WIRELESS_DUMP.txt", "RECEIVER_IP_DUMP.txt", E2E_feedback_buffer, E2E_feedback_wake);
	
		defrag.start();
    }
    
    /*
     * This overloaded method is for testing/debugging purposes only.
     */
    
    public static void go(String data_file, String options_file) {

		optionList opt = new optionList(options_file);
		//opt.print();
	
		packet_buffer = new LinkedList();
		feedback_buffer = new LinkedList();
		feedback_frame_buffer = new LinkedList();
		fragmented_buffer = new LinkedList();
		E2E_feedback_buffer = new LinkedList();
		E2E_feedback_wake = new Object();
		feedback_frame_wake = new Object();
		feedback_fragmented_buffer = new LinkedList();
		feedback_defragmented_buffer = new LinkedList();
		feedback_unfragmented_buffer = new LinkedList();
		feedback_unfragmented_wake = new Object();
		feedback_defragmented_wake = new Object();
		feedback_fragmented_wake = new Object();
		RLE_buffer = new LinkedList();
		defrag_buffer = new LinkedList();
		listener_buffer = new LinkedList();
		frag_wake = new Object();
		RLE_wake = new Object();
		defrag2_wake = new Object();
		feedback_wake = new Object();
		packet_ready = false;
		defrag_wake = new Object();
		listener_wake = new Object();
	
	
		sender = new senderThread(data_file,opt,packet_buffer, frag_wake, E2E_feedback_buffer, E2E_feedback_wake ,"SENDER_IP_DUMP.txt", "E2E_FEEDBACK_DUMP.txt");
		fragmenter = new fragmenterThread(packet_buffer, frag_wake, fragmented_buffer, RLE_wake, opt, "SENDER_WIRELESS_DUMP.txt", "WIRELESS_FEEDBACK_DUMP.txt", feedback_fragmented_buffer, feedback_fragmented_wake);
	
		rle = new RLE(opt);
		sender.start();
		fragmenter.start();
		rle.start();
	
		defrag = new defragmenterThread(opt, "RECEIVER_WIRELESS_DUMP.txt", "RECEIVER_IP_DUMP.txt", E2E_feedback_buffer, E2E_feedback_wake);
	
		defrag.start();
    }

    public static void stop() {
	sender.stop();
	fragmenter.stop();
	rle.stop();
	defrag.stop();
	terminator window = new terminator();
	window.show();
    }
}

















