package src;

/*
 * CS 352 : Group 18 : Phase - 1
 * 
 * Authors : Rajyashri Vasudevamoorthy
 * 			 Daniel Hidalgo
 * 			 Isaac Manayath
 * */



import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//This class contains a information about each peer

public class PeerInfo {

	private int portNumber;
	private String ipAddress;
	private String peerID;
	private ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	/*
	 * Objects retrieved from the HashMap are passed as parameters.
	 * Each object is casted to it's corresponding assignments type.
	 * Constructor that holds information about the peer retracted from the Tracker*/
	
	public PeerInfo(Object object, Object object2,Object o3) {
		// TODO Auto-generated constructor stub
			
			portNumber = (Integer)object; 
			ipAddress = new String(((ByteBuffer) object2).array());
			peerID = new String( ((ByteBuffer) o3).array());
			
			
	//	System.out.println("\n--------PRINTING FROM PEER INFO--------\n");
	//	System.out.println("port "+ portNumber);
	//	System.out.println("ip "+ ipAddress);
	//	System.out.println("pid "+peerID);
	}

	public PeerInfo() {
		// TODO Auto-generated constructor stub
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPeerID() {
		return peerID;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
	//Creates each peer from the hash map response
	public ArrayList<PeerInfo> getPeer(Object o){
		HashMap<ByteBuffer, Object> response = (HashMap<ByteBuffer, Object>) o;

		ByteBuffer peersList = ByteBuffer.wrap(new byte[] {'p','e','e','r','s'});
		ByteBuffer peerID = ByteBuffer.wrap(new byte[] {'p','e','e','r',' ','i','d'});
		ByteBuffer port = ByteBuffer.wrap(new byte[] {'p','o','r','t'});
		ByteBuffer ip = ByteBuffer.wrap(new byte[] {'i','p'});
		
		ArrayList<PeerInfo> data = new ArrayList<PeerInfo>();

		if(response.containsKey(peersList)){
			Object peers = response.get(peersList);


			ArrayList pl1 = (ArrayList) peers;
			final Iterator i = pl1.iterator();
			
			for(int index =0;index<pl1.size();index++){

				//passed the first peer handle to pio
				HashMap<ByteBuffer,Object> hpio = (HashMap<ByteBuffer,Object>) pl1.get(index);
				PeerInfo pi = new PeerInfo(hpio.get(port),hpio.get(ip),hpio.get(peerID));
				data.add(pi);

				//Finding RU11 Peer ip_address to download 
				//Do search

			}
		}
			return data;
	}

	/*public ArrayList<Piece> getPieces() {
		// TODO Auto-generated method stub
		
		return null;
	}*/

	/**
	 * @param pieces the pieces to set
	 */
	public void addPieces(ArrayList<byte[]> pieces) {
		this.pieces = pieces;
		
		
	}
	public PeerInfo getPeer(){
		return this;
	}
	
	
}
