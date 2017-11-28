package src;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloaderPeer implements Runnable{
	//ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	
		private int currentPieceIndex = 1;
		private int currentByteOffset = 0;
		byte id=0;
		int length=0;
		Download d = new Download();
		ArrayList<byte[]> subpieces = new ArrayList<byte[]>();
		MainFrame frame;
		Socket socket;
		DataInputStream isr;
		DataOutputStream ps;
		TorrentInfo ti ;
		PeerInfo peer;
		RandomAccessFile fp;
		public static boolean i_am_interested = false;
		public static boolean i_am_choking = true;
		public PieceStorageManager pm;
		private boolean suspended;
		private boolean isRunning = true;
	
		DownloaderPeer(MainFrame frame,DataInputStream isr,	DataOutputStream ps,Socket s,TorrentInfo torrentInfoObject,PeerInfo peer,RandomAccessFile fp, PieceStorageManager pm){
			
			//super();
			this.pm = pm;
			this.frame = frame;
			System.out.println("I'm thread : " + this.toString());
			
			this.isr = isr;
			this.ps = ps;
			this.socket = s;
			this.ti = torrentInfoObject;
			this.peer = peer;
			this.fp = fp; // do it in RUBTClient.java
			// at RUBTClient create PieceInfo obj
			//initialize have and request arrays
			// once we have all pieces, check for that and then do write to file there.
			System.out.println("my socket is " + socket.toString());

		// once connected gets input , output streams from the socket 
		// initialize ip/op streams
		// generate handshake
		// read msgs
		// send requests and downloadpiece
		// add pieces to the PiecesInformation, piecesArraylist
			try {
				
				sendHandShakeRequestToPeer();
				
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Starting communication with Peer: "+this);
		while(isRunning && (!pm.allPiecesDownloaded())){ 

		try{
			while(frame.pauseResume.getText().equals("Resume")){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

				decodeMessageFromPeer();
				
				sendInterestedMessage();
				readUnChokeResponse();
	
				readPieceData();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		synchronized(this) {
            while(suspended) {
               try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
          }
		}	//end of while	
		//	System.out.println("Download Complete");

			if(pm.allPiecesDownloaded())
				//downloadComplete = true;
				pm.setDownloadComplete(true);
			
	//	this.writeToFile();
		try {
			isr.close();
			ps.close();
			closeSocket();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println("Ending communication with Peer:");
	}
	
	
	private void sendHavePieceMessage(int index) throws Exception {
		// TODO Auto-generated method stub
		int i = index;
	//	for(int i = 0; i <PieceStorageManager.havepieces.length;i++){
		//	if(PieceStorageManager.havepieces[i]){
				System.out.println("***************************************************************************************Writing have for index "+i);
				length=5;
			id=RUBTConstants.haveID;
			ps.writeInt(length);
			if (length > 0) {
				ps.write(id);
				
			}
			ps.writeInt(i);
			ps.flush();
			
			
		//	decodeMessageFromPeer();
			
			//READ RESPONSE FROM PEER
			length = isr.readInt();    
			System.out.println("LENGTH=:"+length);
			if(length>0){
			id = isr.readByte();
			System.out.println(id);
			
			System.out.println("ID : if interested - 2:" + id);}
	//	}
		//}
		
		
	}

	public static byte[] prepareInitialHandshakeRequest(TorrentInfo ti) {
	
		byte[] handshakeRequest = new byte[68];

		int index=0;

		handshakeRequest[index++]= 0x13;

		byte b1[]={'B','i','t','T','o','r','r','e','n','t',' ','p','r','o','t','o','c','o','l'};
		System.arraycopy(b1, 0, handshakeRequest, index, b1.length);
		index += b1.length; // MOVE TE OFFSET LOCATION INT HANDSHALE ARRAY

		byte[] zero = new byte[8];
		System.arraycopy(zero, 0, handshakeRequest, index, zero.length);
		index += zero.length;

		byte b2[]=ti.info_hash.array();
		System.arraycopy(b2, 0, handshakeRequest, index, b2.length);
		index += b2.length;

		byte b3[]= new byte[] {'1','2','5','9','6','7','2','3','4','5','8','6','9','4','2','1','3','3','3','3'};
		System.arraycopy(b3, 0, handshakeRequest, index, b3.length);

		return handshakeRequest;
		
		
		
	}
	public void sendHandShakeRequestToPeer() throws Exception
	{
		byte[] handshakeRequest = prepareInitialHandshakeRequest(ti);
		byte HandshakePrefix = 19;
		System.out.println("Handshake to peer sending");
		ps.write(handshakeRequest);	  // WRITE HANDSHAKE 
		ps.flush();

		byte[] handShakeResponse = new byte[68];
		isr.readFully(handShakeResponse);
		
		byte responsePrefix = handShakeResponse[0];
		
		if(HandshakePrefix != responsePrefix)
		 System.out.println("Not a handshake message");
		byte[] peerHash = new byte[20];
		System.arraycopy(handShakeResponse, 28, peerHash, 0, 20);

		if(!Arrays.equals(peerHash,ti.info_hash.array()))
		{
			System.out.println("Handshake verification failed with Peer");
			System.exit(0);
			
		}
		else{
			System.out.println("Verified Handshake.");
			System.out.println("Handshake Response: " + Arrays.toString(handShakeResponse));
		}
		

	
	}
	 
	public void readBitFieldMessage(int length) throws Exception
	{
		
		byte[] bitfield = new byte[length - 1];
		isr.readFully(bitfield);
		System.out.println("Bypass msg:"+ new String(bitfield));
		
		System.out.println("BYPASSING THE BITFIELD MESSAGES");
		
	}
	
	public void sendInterestedMessage() throws Exception
	{
		System.out.println("Sending INTERESTED message to peer");
		length=1;
		id=RUBTConstants.interestedID;
		ps.writeInt(length);
		if (length > 0) {
			ps.write(id);
			
		}
		ps.flush();
	}
	
	public void readUnChokeResponse() throws Exception {
		//READ THE UNCHOKE
				int unchoke = isr.readInt();
				System.out.println("Peer: Unchoke ID:"+unchoke);
				byte resp = isr.readByte();
				System.out.println("Peer: unchoke message:"+resp);
				System.out.println(resp);
	}
	public void readPieceData() throws Exception
	{
		int r =0;
		int pieceIndex = 0;
		int last_piece_num = ti.piece_hashes.length;
		int dif = ti.piece_hashes.length - 1;
		int lastPieceSize = ti.file_length - (dif * ti.piece_length);
		int pieceSize = 16384;
		
		System.out.println("Before request");
		
		while(isRunning & !pm.allPiecesDownloaded()){ //
			while(frame.pauseResume.getText().equals("Resume")){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
			int index1 = 0;
			int offsetBegins = 0;
			
		
			
			if((pieceIndex = pm.pieceToDownload()) != -1){ 
			
	System.out.println("I'm on thread: " + this.toString());

			System.out.println("Requesting peer for "+ pieceIndex+"th piece of the file");	
				pm.accessToRequestPiece[pieceIndex] = true;
				
			
				while(index1<2 ){
				
					byte[] message0 = new byte[17];
				
					if((pieceIndex+1) == last_piece_num){
					 
							if(index1 == 0)
								pieceSize = 16384; 
							if(index1 == 1)
								pieceSize  = lastPieceSize -16384;
							
					}				
			
					System.arraycopy(intToByteArray(13), 0, message0, 0, 4); //<length prefix> is 13
					message0[4] = (byte) 6; //message ID is 6
					System.arraycopy(intToByteArray(pieceIndex), 0, message0, 5,4); // set index payload
				
					System.arraycopy(intToByteArray(offsetBegins), 0, message0, 9,4); // set begin payload
					System.arraycopy(intToByteArray(pieceSize), 0, message0,13, 4); // set length payload
					ps.write(message0);
					ps.flush();
				
				/* piece: <len=0009+X><id=7><index><begin><block>
					index: integer specifying the zero-based piece index
					begin: integer specifying the zero-based byte offset within the piece
					block: block of data, which is a subset of the piece specified by index.*/
				  
					r = isr.readInt();
					System.out.println("piece length < len = 0009 + X>: "+r); // < len = 0009 + X> , X = lenth of block
					byte big = isr.readByte();
					System.out.println("ID: "+big); // id = 7 , 1 byte
				
					byte[] response0 = new byte[pieceSize];							//byte[] response0 = new byte[16384];
									
					int r1 = isr.readInt();	//4 byte piece index
					System.out.println("Index :" + r1);
				
					int r11 = isr.readInt(); // 4 byte offset
					System.out.println("Begin :" + r11);
		
					for (int i = 0; i < pieceSize; i++) {
						response0[i] = isr.readByte();
					} // save data
				
					subpieces.add(response0);
				
					if(index1 == 1)
						updatePieces(pieceIndex);
				
					offsetBegins = 16384; 
					index1 = index1+1; // go for next subpiece
					System.out.println("Response2 "+response0.toString() );
				
				
				}
				
				index1 = 0;
				offsetBegins = 0;
			}
			
			if(pm.allPiecesDownloaded())
				//downloadComplete = true;
				pm.setDownloadComplete(true);
						
		}
		
	}
	private void closeSocket() throws IOException {
		// TODO Auto-generated method stub
		socket.close();
	}


	public static byte[] toByteArray(byte[] fullpiece2) {
	    final int n = fullpiece2.length;
	    System.out.println("SIZE: "+ n);
	    byte ret[] = new byte[n];
	    //in.get(1).length;
	    /*for (int i = 0; i < n; i++) {
	        ret[i] = 
	    }*/
	    return ret;
	}
	
	
	
	
	
	public void decodeMessageFromPeer() throws Exception
	{
		while(frame.pauseResume.getText().equals("Resume")){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Inside Decode");
		int length = isr.readInt();
		System.out.println("found len");
		byte id = isr.readByte();
		System.out.println(id);
		try {
		switch (id) {
		case (RUBTConstants.chokeID):
			sendKeepAlive();
		case (RUBTConstants.unchokeID):
			if(i_am_interested){
				System.out.println("Sending interestedMessage");
				sendInterestedMessage();
				i_am_interested = false;
			}
				
		
		case (RUBTConstants.interestedID):
			System.out.println("Peer is interested => send unchoke");
			sendUnchokeMessage();
		case (RUBTConstants.uninterestedID):			
			
		case (RUBTConstants.haveID):
			
		case (RUBTConstants.bitfieldID):
			readBitFieldMessage(length);
		case (RUBTConstants.pieceID):
			
		case (RUBTConstants.requestID):
			
		}
		}
		catch(Exception e){e.printStackTrace();}
	
	}
	private void sendKeepAlive() throws IOException {
		// TODO Auto-generated method stub
		int length=0;
		ps.writeInt(length);
	}

	private void sendUnchokeMessage() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("***************************************************************************************Unchoking");
		int length=1;
	id=RUBTConstants.unchokeID;
	ps.writeInt(length);
	if (length > 0) {
		ps.write(id);
		
	}
	//ps.writeInt(i);
	ps.flush();
	}

	public synchronized void updatePieces(int index) throws Exception{
		
		byte[] fullPiece;
		
		int size = 0;
		int count = 0;
		int i;

	//	PieceStorageManager.requestedpieces[index] = true;//this.requests[index] = true; 111

		for (i = 0; i < this.subpieces.size(); i++) {
			size += this.subpieces.get(i).length;
		}
		
		if(index+1 == ti.piece_hashes.length){
			System.out.println("Size of stored piece: " + size);
			System.out.println("Original piece lenth: ");
		}
		
		fullPiece = new byte[size];

		for (i = 0; i < this.subpieces.size(); i++) {
			System.arraycopy(this.subpieces.get(i),0,fullPiece,count,this.subpieces.get(i).length);
			count += this.subpieces.get(i).length;
		}
	
		//verify hash
		if(verifyHash(fullPiece, this.ti.piece_hashes[index].array()) == true){
			//hashes match
		}else{
			//hashes do not much
			return;
		}
	
		// send bytes[] to writeDatatoarray() wraps full piece and puts into main piece array
	
		pm.writeDataToArray(fullPiece, index);
	
		this.subpieces = new ArrayList<byte[]>();
	//	PieceStorageManager.numberofPiecesDownloaded ++;
	}
	public boolean verifyHash(byte[] piece, byte[]hash)throws Exception{
		byte temp[];
		
		MessageDigest x = MessageDigest.getInstance("SHA-1");
		temp = x.digest(piece);
		x.update(temp);
		
		if(Arrays.equals(temp,hash)){System.out.println("---------HASHES MATCH-----"); return true; 
		}
		else{System.out.println("---------HASHES DON't MATCH-----"); return false;}
	}
	public static byte[] intToByteArray(int value) {
		byte[] retVal = new byte[4];
		retVal[0] = (byte) (value >> 24);
		retVal[1] = (byte) (value >> 16);
		retVal[2] = (byte) (value >> 8);
		retVal[3] = (byte) (value);
		return retVal;
	}
	private byte[] toBytes(char[] chars) {
	    CharBuffer charBuffer = CharBuffer.wrap(chars);
	    ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
	    byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
	            byteBuffer.position(), byteBuffer.limit());
	    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
	    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
	    return bytes;
	}

	public void sendNextPayloadRequest() throws Exception
	{
		int piece_length = ti.piece_length;
		int file_length =ti.file_length;
		int numPieces = ti.piece_hashes.length;
		int requestSize=0;
		
		if(this.currentPieceIndex == (numPieces - 1)){
			piece_length = file_length % piece_length;
		}
		
		if((this.currentByteOffset + RUBTConstants.requestSize) > piece_length){
			requestSize = piece_length % RUBTConstants.requestSize;
		}
		
	
		
		if((this.currentByteOffset + requestSize) >= piece_length){
			this.currentPieceIndex = -1;
			this.currentByteOffset = 0;
		} else {
			this.currentByteOffset += requestSize;
		}
		
		ps.writeInt(currentPieceIndex);
		ps.writeInt(currentByteOffset);
		ps.writeInt(requestSize);
		
	}
	
	public static boolean verifySHA1(byte[] piece, ByteBuffer SHA1Hash, int index) {
		MessageDigest SHA1;

		try {
			SHA1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Unable to find SHA1 Algorithm");
			System.out.println("Unable to find SHA1 Algorithm");
			return false;
		}
		
		SHA1.update(piece);
		byte[] pieceHash = SHA1.digest();
		System.out.println("PieceHash :" + pieceHash.toString() + " Index " + index);
		System.out.println("OurHash :" + SHA1Hash.array() + "Index " + index);

		if (Arrays.equals(pieceHash, SHA1Hash.array())) {
			System.out.println("Verified - " + SHA1.digest() + " - " + SHA1Hash.array() + " for index " + index);
			return true;
		} else {
			return false;							
		}
	}
	
	void suspend() {
	      suspended = true;
	}
	synchronized void resume() {
	      suspended = false;
	       notify();
	}
	void stop(){
		isRunning = false;
	}
	
}
	

