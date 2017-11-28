package src;



import java.nio.ByteBuffer;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class PieceStorageManager {
	
//	public static int[] data = new int[50];
	/**
	 * 
	 * */
	public static ByteBuffer[] pieceList;
	private static byte[] pieces;
	
	static int totalNumberOfPieces  = 436; //TODO : change it ti.numofpieces
	
	public static boolean[] isPieceExistArray = new boolean[totalNumberOfPieces];
	public static boolean[] accessToRequestPiece = new boolean[totalNumberOfPieces];
	public static boolean[] accessToUploadPiece = new boolean[totalNumberOfPieces];
	public static int[] uploadAccessRequestedQueue ;
	public static boolean[] isPieceUploaded = new boolean[totalNumberOfPieces]; // not needed??
	public static boolean haveAllPieces = false;
	public static boolean downloadComplete = false;
	private static int numberOfPiecesDownloaded ;
	public static int sizeDownloaded;
	public static int[] uploadMax = new int[totalNumberOfPieces];
	
	
	
	public PieceStorageManager(){
		init();
	}
	public  void init(){
		
		pieceList = new ByteBuffer[totalNumberOfPieces];
		sizeDownloaded = 0;
		numberOfPiecesDownloaded =0;
		
		for(int i = 0; i< totalNumberOfPieces;i++){
			
			isPieceExistArray[i] = false;
			accessToRequestPiece[i] = false;
			uploadMax[i] = 0;
		}
		
		//numberOfPieces = 0;
		
	}
	private static boolean check() {
		// TODO Auto-generated method stub
		for(int i = 0 ; i< totalNumberOfPieces ;i++ )
		{
			if(isPieceExistArray[i] != true)
				return false;
		}
			
		return  true;
	}
	
	public static boolean allPiecesDownloaded(){
		
		haveAllPieces = check();
		
		return haveAllPieces;
		
	}
	
	public synchronized static int pieceToDownload(){
		
		//int index = 0;
		for(int index = 0; index < totalNumberOfPieces; index++){
			if(!isPieceExistArray[index] & !accessToRequestPiece[index]){
				accessToRequestPiece[index] = true;
				return index;
			}
		}
		return -1;
		
	}
	/**
	 * @return the downloadComplete
	 */
	public synchronized static boolean isDownloadComplete() {
		return downloadComplete;
	}
	/**
	 * @param downloadComplete the downloadComplete to set
	 */
	public synchronized static void setDownloadComplete(boolean Boolean) {
		//if(!downloadComplete)
			downloadComplete = Boolean;
	}
	public synchronized void  writeDataToArray(byte[] pieces,int index) { //synchronized
		// TODO Auto-generated method stub
		
			ByteBuffer fullpiece = ByteBuffer.wrap(pieces);
			pieceList[index] = fullpiece;
			
//			System.out.println("Writing Data at index "+i+" by thread ");
			//data[i] = i;
			System.out.println("Val at "+index+" is "+ pieceList[index]);
			isPieceExistArray[index] = true;
			setNumberOfPiecesDownloaded(getNumberOfPiecesDownloaded() + 1) ;
			sizeDownloaded = sizeDownloaded + pieces.length;

			
		
	}
	public synchronized ByteBuffer readData(int index) { // index is the piece Number requested by the peer
		// TODO Auto-generated method stub
		
		//int index = data[i];
		
		ByteBuffer readPiece = pieceList[index];   // think about converting from bytebuffer to byte[] from here or caller?
		
		System.out.println("Value at "+index +" is "+ readPiece);
		
		return readPiece;
		
	}

	/*public synchronized static int pieceToDownload(){
		
		//int index = 0;
		for(int index = 0; index < 50; index++){
			if(!isPieceExistArray[index]){
		//		accessToRequestPiece[index] = true;
				return index;
			}
		}
		return -1;
		
	}
	 * public synchronized static boolean getAccess(int index){
		
			return accessToRequestPiece[index];
		
	}
	
	public synchronized static void setAccess(int index){
		
	//	if(accessToRequestPiece[index])
			accessToRequestPiece[index] = false;
		
	}
*/
	public synchronized static boolean getAccessToRequestPiece(int index){
		
		if(accessToRequestPiece[index])
			accessToRequestPiece[index] = false;
		else
			accessToRequestPiece[index] = true;
		
		return accessToRequestPiece[index];
	}
public synchronized static boolean getrequestUploadPiece(int index){
		
		if(uploadMax[index] > 2)
			return false;
		if(accessToUploadPiece[index])
			accessToUploadPiece[index] = false;
		else
			accessToUploadPiece[index] = true;
		
		return accessToUploadPiece[index];
	}
	public synchronized static int getDownloadedSize(){
		
		
		
		return sizeDownloaded ;
		
	}
	/**
	 * @return the numberOfPiecesDownloaded
	 */
	public synchronized static int getNumberOfPiecesDownloaded() {
		return numberOfPiecesDownloaded;
	}
	/**
	 * @param numberOfPiecesDownloaded the numberOfPiecesDownloaded to set
	 */
	public synchronized static void setNumberOfPiecesDownloaded(int numberOfPiecesDownloaded) {
		PieceStorageManager.numberOfPiecesDownloaded = numberOfPiecesDownloaded;
	}
	
	public synchronized static int getHavePiece(){
		
		for(int i = 0 ; i < 436 ; i++){
			if(isPieceExistArray[i])
				return i;
		}
		
		return -1;
		
	}

}
