import java.io.FileWriter;


public class SimonTest {
	
	public static FileWriter senderDebugWriter, fragmenterDebugWriter, defragmenterDebugWriter;
	
	public static final int senderFlag = 0;
	public static final int fragmenterFlag = 1;
	public static final int defragmenterFlag = 2;
	
	static {
		try {
			senderDebugWriter = new FileWriter("senderDebug");
			fragmenterDebugWriter = new FileWriter("fragmenterDebug");
			defragmenterDebugWriter = new FileWriter("defragmenterDebug");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		startup test = new startup();
		test.go("src/datafile", "simonSettings");

	}
	
	public static void log(int flag, String msg) {
		FileWriter myWriter;
		switch(flag) {
			case senderFlag:
				myWriter = senderDebugWriter;
				break;
			case fragmenterFlag:
				myWriter = fragmenterDebugWriter;
				break;
			case defragmenterFlag:
				myWriter = defragmenterDebugWriter;
				break;
			default:
				System.out.println("Unrecognized flag sent to SimonTest.log()");
				return;
		}
		try {
			myWriter.write(msg + "\n");
			myWriter.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String packetToString(byte[] packet, int headerSize) {
		String returnMe = "<  ";
		for(int i = 0; i < headerSize; i++) {
			if(i != 0 && i % 4 == 0) {
				returnMe += " ";
			}
			returnMe += "" + (int) packet[i] + " ";
		}
		returnMe += " > ";
		returnMe += bytesToString(packet, headerSize);
		return returnMe;
	}
	
	public static String bytesToString(byte[] bytes, int offset) {
		String returnMe = "";
		for(int i = offset; i < bytes.length; i++) {
			returnMe += byteToChar(bytes[i]);
		}
		return returnMe;
	}
	
	public static String bytesToString(byte[] bytes) {
		return bytesToString(bytes, 0);
	}
	
	public static String byteToChar(byte b) {
		String returnMe = "";
        if (b < 0) {
            returnMe += ("M-");
            b += 0x80;                // Make b positive
        }
        if (b >= ' ' && b <= '~') {
            return returnMe + (char) b;
        }
        switch (b) {
            case '\0': return returnMe + "\\0";
            case '\n': return returnMe + "\\n";
            case '\r': return returnMe + "\\r";
            case '\b': return returnMe + "\\b";
            case 0x7f: return returnMe +"\\?";
            default:   return returnMe + (char) b + '@';
        }
    }

}
