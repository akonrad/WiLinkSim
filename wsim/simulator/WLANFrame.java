package simulator;

public class WLANFrame {

	// 802.11 frame control fields
	
	private int 	protocolVersion;
	private int 	type;				// The type of WLAN frame
	private int 	subtype;			// Combined w/ type determines the exact type of frame
	private int 	toAP;
	private int 	fromAP;
	private int 	moreFrag;
	private int 	retry;
	private int 	powerMgt;
	private int 	moreData;
	private int 	wep;
	private int 	rsvd;
	
	// 802.11 frame header fields 
	
	private int[]	frameControl;
	private int 	duration;
	private int 	address1;	// MAC address of station that receives the frame
	private int 	address2;	// MAC address of station that transmits the frame
	private int 	address3;	// MAC address of BSS
	private int 	seqControl;
	private int 	address4;	// Used when APs forward frames to each other in ad hoc mode
	private int 	crc;		// Cyclic redundancy check
	
	// 802.11 data
	
	private IPPacket payload;	// Consists of IP packet
	
	// Constructor
	
	public WLANFrame(int seqNum, IPPacket ipPacket) {
		
		// 802.11 frame control fields
				
		protocolVersion	= 0;
		type 			= 0;		
		subtype 		= 0;	
		toAP			= 0;
		fromAP			= 0;
		moreFrag		= 0;
		retry			= 0;
		powerMgt		= 0;
		moreData		= 0;
		wep				= 0;
		rsvd			= 0;
		
		frameControl	= new int[11];
		frameControl[0]	= protocolVersion;
		frameControl[1]	= type;		
		frameControl[2]	= subtype;	
		frameControl[3]	= toAP;
		frameControl[4]	= fromAP;
		frameControl[5]	= moreFrag;
		frameControl[6]	= retry;
		frameControl[7]	= powerMgt;
		frameControl[8]	= moreData;
		frameControl[9]	= wep;
		frameControl[10]= rsvd;

		// 802.11 frame header fields 
		
		duration		= 0;
		address1		= 0;	
		address2		= 0;	
		address3		= 0;	
		seqControl		= seqNum;
		address4		= 0;	
		crc				= 0;		
		
		// 802.11 data
		
		payload			= ipPacket;				
	}
}
