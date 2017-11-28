package src;



import java.nio.ByteBuffer;

public interface RUBTConstants {
		

	public static final byte keepAliveID = -1;	
	public static final byte chokeID = 0;	
	public static final byte unchokeID = 1;
	public static final byte interestedID = 2;
	public static final byte uninterestedID = 3;
	public static final byte haveID = 4;
	public static final byte bitfieldID = 5;
	public static final byte requestID = 6;
	public static final byte pieceID = 7;
	public static final byte cancelID = 8;
	public static final byte portID = 9;
	public static final int requestSize = 16000;
	
	
	
}
