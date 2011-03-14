package simulator;

/**
 * The IPPacket class creates an IPv4 packet, which consists of a header and data section
 * 
 * @author	Kat Villariba
 * @version	1.0 2011-MAR-13
 */
public class IPPacket {
	
	// IP version constants
	
	public static int		IPV4			= 0;
	
	// IP flag constants
	
	public static int		RESERVED		= 0;
	public static int		DONT_FRAGMENT	= 1;
	public static int		MORE_FRAGMENTS	= 2;
	
	// IP protocol constants
	
	public static int		TCP				= 06;
	public static int		UDP				= 17;

	// IP header fields
	
	private int		version;			// IPv4
	private int		headerLength;
	private int		dscp;				// Differentiated Services Code Point
	private int		ecn;				// Explicit Congestion Notification 
	private int		totalLength;		// Length of header and data
	private int		identification;		// Identifies fragments of original IP datagram
	private int		flags;				// Identifies fragments
	private int		fragmentOffset;		// Offset of fragment relative to beginning of original IP datagram
	private int		ttl;				// Time to live
	private int		protocol;			// Defines protocol used in the data portion of IP datagram
	private int		headerChecksum;		// Used for error-checking the header
	private int		sourceAddress;
	private int		destinationAddress;

	// IP data

	private byte[] 	data;
	
	// Constructor
	
	public IPPacket(int i_identification,byte[] i_Data) {
		version				= IPV4;
		headerLength		= 20;
		dscp				= 0;
		ecn					= 0;
		totalLength			= i_Data.length + headerLength;
		identification		= i_identification;
		flags				= DONT_FRAGMENT;
		fragmentOffset		= 0;
		ttl					= 0;
		protocol			= UDP;
		headerChecksum		= 0;			// TODO: Compute header checksum
		sourceAddress		= 0;			// TODO: Enter valid source address
		destinationAddress	= 1;			// TODO: Enter valid destination address
		data				= i_Data;
	}
	
	// Getter Methods

	public byte[] getData() {
		return data;
	}
	
	public int getDataLength() {
		return totalLength - headerLength;
	}
	
	public int getVersion() {
		return version;
	}
	
	public int getHeaderLength() {
		return headerLength;
	}

	public int getDSCP() {
		return dscp;
	}

	public int getECN() {
		return ecn;
	}

	public int getIdentification() {
		return identification;
	}

	public int getFlags() {
		return flags;
	}

	public int getFragmentOffset() {
		return fragmentOffset;
	}

	public int getTTL() {
		return ttl;
	}

	public int getProtocol() {
		return protocol;
	}

	public int getHeaderChecksum() {
		return headerChecksum;
	}

	public int getSourceAddress() {
		return sourceAddress;
	}

	public int getDestinationAddress() {
		return destinationAddress;
	}
	
}
