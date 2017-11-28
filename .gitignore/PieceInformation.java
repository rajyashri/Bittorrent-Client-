package src;



import java.nio.ByteBuffer;
import java.util.ArrayList;


public class PieceInformation {
	
	public static ByteBuffer[] pieceList;
	public static boolean[] havepieces = null;
	public static boolean[] requestedpieces = null;
	//public static ArrayList<ByteBuffer[]> pieces;
	
	public static int numberofpieces;
	private static byte[] pieces;
	public static int numberofPiecesDownloaded = 0;
	public static int numberofPiecesUploaded =0;
	
	
	public PieceInformation(){
		
	}
	public PieceInformation(int numberofpieces){
		
		pieceList = new ByteBuffer[numberofpieces];
		havepieces = new boolean[numberofpieces];
		for(int i = 0 ; i< numberofpieces;i++)
			havepieces[i] = false;
		requestedpieces = new boolean[numberofpieces];
		//pieces = new ArrayList<ByteBuffer[]>(numberofpieces);
		for(int i = 0 ; i< numberofpieces;i++)
			requestedpieces[i] = false;
		numberofpieces = numberofpieces;
		
	}
	
	public void setPieceList(int index, ByteBuffer[] pieces2) {
		
		
	}
	public void addPiecesToList(ByteBuffer[] piece){
	//	pieceList.add(piece);
	}
	public ArrayList<ByteBuffer[]> getPieceList(){
		return null;
		//return pieceList;
		
	}


	public boolean getHavepieces(int index) {
		return havepieces[index];
	}


	public synchronized void setHavepieces (int index, boolean Boolean) {
		havepieces[index] = Boolean;
		numberofPiecesDownloaded++;
		return;
	}

	public ByteBuffer getRequestedpiece(int index) {
		return pieceList[index];
	}
	public boolean requestPiece(int index){
		return requestedpieces[index];
				
	}
	public void setRequestedPiece(int index, boolean Boolean){
		requestedpieces[index] = true;
	}

	public int getNumberOfPieces(){
		return numberofpieces;
	}
	public byte[] getPiece(int i1) {
		// TODO Auto-generated method stub
		
		return null;
	}
	/**
	 * @return the pieces
	 */
	public static byte[] getPieces(int index) {
		
		return pieces;
	}//not needed
	/**
	 * @param pieces the pieces to set
	 */
	public void setPieces(int index, ByteBuffer[] pieces) {
		//PieceInformation.pieces = pieces;
		//setPieceList(index, pieces);
		
	}//not needed
	public synchronized void writeToFile(){
		try {
			RUBTClient.writeOutputToFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
