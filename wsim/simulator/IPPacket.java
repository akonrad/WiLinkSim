package simulator;

public class IPPacket {
	
	// IP header field constants
	
	public static int		IPV4			= 0;
	public static int		UDP				= 11;
	public static int		TCP				= 06;

	// IP flag constants
	
	public static int		RESERVED		= 0;
	public static int		DONT_FRAGMENT	= 1;
	public static int		MORE_FRAGMENTS	= 2;
	
	// IP header fields
	
	private Integer 	version;			// IPv4
	private Integer 	headerLength;
	private Integer 	dscp;				// Differentiated Services Code Point
	private Integer 	ecn;				// Explicit Congestion Notification 
	private Integer 	totalLength;		// Length of header and data
	private Integer 	identification;		// Identifies fragments of original IP datagram
	private Integer 	flags;				// Identifies fragments
	private Integer 	fragmentOffset;		// Offset of fragment relative to beginning of original IP datagram
	private Integer 	ttl;				// Time to live
	private Integer 	protocol;			// Defines protocol used in the data portion of IP datagram
	private Integer 	headerChecksum;		// Used for error-checking the header
	private Integer 	sourceAddress;
	private Integer 	destinationAddress;

	// IP data

	private byte[] 		data;
	
	// Constructor
	
	public IPPacket(byte[] myData) {
		version				= IPV4;
		headerLength		= 20;
		dscp				= 0;
		ecn					= 0;
		totalLength			= myData.length + headerLength;
		identification		= 0;
		flags				= DONT_FRAGMENT;
		fragmentOffset		= 0;
		ttl					= 0;
		protocol			= UDP;
		sourceAddress		= 0;
		destinationAddress	= 0;
	}
	
	// Methods

	public byte[] getHeader() {
		byte[] header = new byte[headerLength];
		
		header[0]  = version.byteValue();
		header[1]  = headerLength.byteValue();
		header[2]  = dscp.byteValue();
		header[3]  = ecn.byteValue();
		header[4]  = totalLength.byteValue();
		header[5]  = identification.byteValue();
		header[6]  = flags.byteValue();
		header[7]  = fragmentOffset.byteValue();
		header[8]  = ttl.byteValue();
		header[9]  = protocol.byteValue();
		header[10] = sourceAddress.byteValue();
		header[11] = destinationAddress.byteValue();
		
		return header;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getHeaderLength() {
		return headerLength;
	}
	
	public int getDataLength() {
		return totalLength - headerLength;
	}

}
