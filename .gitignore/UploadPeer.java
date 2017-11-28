package src;



import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;


public class UploadPeer implements Runnable {
	Socket client = null;
	DataInputStream dinput = null;
	DataOutputStream doutput = null;
	InputStream input = null;
	OutputStream output = null;
	TorrentInfo ti;
	PieceStorageManager pm ;
	PeersUploadRecord newPeer;
	int length;
	byte id;
	MainFrame frame;
	public UploadPeer(MainFrame frame, Socket connection,TorrentInfo ti, PieceStorageManager pm, PeersUploadRecord newPeer) throws IOException {
	//	super();
		this.frame = frame;
		client = connection;
		input = client.getInputStream();
		output = client.getOutputStream();
		doutput = new DataOutputStream(output);
		dinput = new DataInputStream(input);
		
			this.ti = ti;
			this.pm = pm;
			this.newPeer = newPeer;
			
			
		
	}

	@Override
	public void run() {
		if(client.isBound()){
		//handshake
			System.out.println("_________________________________________________________________Connected");
			System.out.println("Inet address: " + client.getInetAddress());
			try {
				receiveHandshake();
				sendHandShakeRequestToPeer();
			//	sendUnchokeMessage();
				System.out.println("Sending HavePiece");
				sendHavePieceMessage();
				decodeMessageFromPeer();
				readRequest();
				
		//	System.out.println("Reading msg from peer");
		//		readInterestedMessageFromPeer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//requestmessages
		
		}
	}

	private void readRequest() throws IOException {
				// TODO Auto-generated method stub
		while(newPeer.uploadMax < 4){
		
		length = dinput.readInt();    
		System.out.println("LENGTH=:"+length);
		id = dinput.readByte();
		while(id == 6){
			while(frame.pauseResume.getText().equals("Resume")){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
		System.out.println("ID:" + id);
		// index, begin, length
		int index = dinput.readInt();
		System.out.println("Index :" + index);
		int begin = dinput.readInt();
		System.out.println("Begin :" + begin);
		int len = dinput.readInt();
		System.out.println("Length of piece requested " + len);
		//length = dinput.readInt();    
		
		///// Uploading the requested piece
		/* piece: <len=0009+X><id=7><index><begin><block>
		index: integer specifying the zero-based piece index
		begin: integer specifying the zero-based byte offset within the piece
		block: block of data, which is a subset of the piece specified by index.*/
	  
	//	if(PieceStorageManager.getrequestUploadPiece(index))
		//{
			//PieceStorageManager.accessToUploadPiece[index] = true;
			System.out.println("Sending piece%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.out.println("upload peer ipAddress is  " + newPeer.ipAddress);
			byte[] piecerequested =  PieceStorageManager.pieceList[index].array();   //PieceInformation.getPieces(index);
			
			byte[] uploadPiece = new byte[len];
			doutput.writeInt(9+len); //length prefix
	//		doutput.flush();
			doutput.write(7);//id
		//	doutput.flush();
			doutput.writeInt(index);
			doutput.writeInt(begin);
			System.arraycopy(piecerequested, begin, uploadPiece, 0, len);
			doutput.write(uploadPiece); // payload
			doutput.flush();
			
			//pm.isPieceUploaded[index] = true;
		//	pm.uploadMax[index]++;
		//}
			System.out.println("Uploaded piece "+ index);
			newPeer.uploadMax++;
			Tracker.uploaded = Tracker.uploaded + uploadPiece.length; // updates updoaded length to Tracker
			//so that when tracker is updated periodically, the updated value will be recorded.
			newPeer.setSizeOfDataUploadedToPeer(uploadPiece.length);
			length = dinput.readInt();
			System.out.println("LENGTH=:"+length);
			id = dinput.readByte();
		
		}
		}
		// send choke so that other peers could get chance to upload
		
		sendChokeMessageToPeer();
		
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			sendUnchokeMessage();
			newPeer.uploadMax = 0;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
					decodeMessageFromPeer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
	}
	private void sendChokeMessageToPeer() {
		// TODO Auto-generated method stub
		System.out.println("Sending Unchoke message to peer");
		length=1;
		id=RUBTConstants.chokeID;
		try {
			doutput.writeInt(length);
		
			if (length > 0) {
			doutput.write(id);
			
			}
			doutput.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private void receiveHandshake() throws IOException {
		// TODO Auto-generated method stub
		byte[] handShakeResponse = new byte[68];
		dinput.readFully(handShakeResponse);
		byte HandshakePrefix = 19;
		byte responsePrefix = handShakeResponse[0];
		
		if(HandshakePrefix != responsePrefix)
		 System.out.println("Not a handshake message");
		byte[] peerHash = new byte[20];
		System.arraycopy(handShakeResponse, 28, peerHash, 0, 20);

		if(!Arrays.equals(peerHash,ti.info_hash.array()))
		{
			System.out.println("Handshake verification failed with Peer");
		//	System.exit(0);
			
		}
		else{
			System.out.println("Verified Handshake.");
		//	System.out.println("Handshake Response: " + Arrays.toString(handShakeResponse));
		}
		
	//	System.arraycopy(this.ti.info_hash.array(),0,handShakeResponse,28, 20);
		
	//	doutput.write(handShakeResponse);
		
	}
	private void sendHavePieceMessage() throws Exception {
		// TODO Auto-generated method stub
		int i = 0 ;//= index;
	//	while((i = PieceStorageManager.getHavePiece()) != -1){ //ti.filelength
		
		for(i = 0 ; i< pm.getNumberOfPiecesDownloaded();i++){
			if(pm.isPieceExistArray[i]){
			System.out.println("******sending have for index "+i);
				length=5;
				id=RUBTConstants.haveID;
				doutput.writeInt(length);
		//	if (length > 0) {
				doutput.write(id);
				
		//	}
			doutput.writeInt(i);
			doutput.flush();
			}
		//	i++;
		}
		//}
	}
	public void readInterestedMessageFromPeer() throws IOException{		
		//	decodeMessageFromPeer();
			
			//READ RESPONSE FROM PEER
			
			length = dinput.readInt();    
			System.out.println("LENGTH=:"+length);
			while(length == 0){
				System.out.println("Sending keep Alive since received 0");
				//int length=0;
				doutput.writeInt(0); // sending keep alive
				System.out.println("Response received: " + length);
				length = dinput.readInt();    
			}
				id = dinput.readByte();
			System.out.println(id);
			
			System.out.println("ID : if interested - 2:" + id); //}
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
		doutput.write(handshakeRequest);	  // WRITE HANDSHAKE 
		doutput.flush();

	}
	public void sendUnchokeMessage() throws Exception
	{
		System.out.println("Sending Unchoke message to peer");
		length=1;
		id=RUBTConstants.unchokeID;
		doutput.writeInt(length);
		if (length > 0) {
			doutput.write(id);
			
		}
		doutput.flush();
	}
	public void decodeMessageFromPeer() throws Exception
	{
		System.out.println("Inside Decode");
		int length = dinput.readInt();
		System.out.println("found len " + length);
		while(length == 0){
			sendHavePieceMessage();
			length = dinput.readInt();
		}
		
		byte id = dinput.readByte();
		System.out.println(id);
		try {
		switch (id) {
		case (RUBTConstants.chokeID):
			sendKeepAlive();break;
		case (RUBTConstants.unchokeID):
			//if(i_am_interested){
				System.out.println("Sending interestedMessage");
				//sendInterestedMessage();
				//i_am_interested = false;
			//}
				
		break;
		case (RUBTConstants.interestedID):
			System.out.println("Peer is interested => send unchoke");
			sendUnchokeMessage();
			break;
		case (RUBTConstants.uninterestedID):			
			break;
		case (RUBTConstants.haveID):
			break;
		case (RUBTConstants.bitfieldID):
			readBitFieldMessage(length); break;
		case (RUBTConstants.pieceID):
			break;
		case (RUBTConstants.requestID):
			break;
		}
		}
		catch(Exception e){e.printStackTrace();}
	
	}

	private void sendKeepAlive() {
		// TODO Auto-generated method stub
		
	}

	private void readBitFieldMessage(int length2) {
		// TODO Auto-generated method stub
		
	}
}
