package src;







import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class RUBTClient {

	private static String my_Peer_ID = "12596723458694213333";
	private static RandomAccessFile fp; 
	private static byte[] torrentContent;
	public static int PEER_PORT_NUMBER;
	public static PieceStorageManager pi;
	public static String STORE_TO_FILE;
	static Tracker t = null;
	static TorrentInfo ti;
	static ArrayList<PeerInfo> data; static String announce_url = "";
	public static ClientHandler ch; 
	public static ArrayList<PeerConnection> listOfConnectedPeers = new ArrayList<PeerConnection>();
	public static void main(String[] args) throws Exception {
		RUBTClient rubt = new RUBTClient();
		if(args.length == 0){
			System.out.println("No arguments in server field");
		}
		else if(args.length == 1){
			System.out.println("Not enough arguments provided");
		}
		else if(args.length == 2){

			String torrentFile = args[0];
			String downloadFileName = args[1];
			
			FileHandler fh = new FileHandler(torrentFile);
			
			fp = fh.openFile(); // opens the torrent file in read/write mode
			STORE_TO_FILE =downloadFileName;

			if(fp == null)
				System.out.println("File open failed");
			else{
				System.out.println("Torrent File successfully opened");
				torrentContent = fh.readFile(fp);
				try {
					ti = new TorrentInfo(torrentContent);
					ch = new ClientHandler(ti);
				//	 pi = new PieceInformation(ti.piece_hashes.length);
					 pi = new PieceStorageManager();
					 //ti.piece_hashes.length);
					 pi.init();
					 
					//establish connection with tracker
					 t = new Tracker(ti,my_Peer_ID,6884,ch.getInfo_hash());
					 
					//t.event = "started";
					data = t.updateTracker();//t.getPeerList();
				//	Upload u = new Upload();
				//	u.startMyPeer();
					MainFrame frame = new MainFrame(ti,data,downloadFileName);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.pack();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
					ArrayList<PeerInfo> valid_peer_list = findPeer(data);
				//	String ru11_ip = null;// = findPeer(data);  // finding ru11 peer
					for(PeerInfo peer: valid_peer_list){
						
						String	ru11_ip = peer.getIpAddress();
						String ru11PeerID = peer.getPeerID();
						int ru11Port = peer.getPortNumber();
						System.out.println("calling startPeerConnection");
						
						PeerConnection pc_obj = startPeerConnection(frame,ru11_ip,ti,ru11Port,ru11PeerID,downloadFileName,t,pi);//t.getPeerId()
						//System.out.println(status);
						listOfConnectedPeers.add(pc_obj);
					}
					t.event = "started";
					//	t.downloaded = ti.file_length; t.uploaded = 0 ;
					t.left =ti.file_length - Tracker.downloaded ;
					data = t.updateTracker();//t.getPeerList();
					UploadConnect uc = new UploadConnect(frame,ti,pi,6884,"128.6.171.132");
				//	UploadConnect uc1 = new UploadConnect(ti,pi,6885,"128.6.171.130");
					Thread tu = new Thread(uc,"UploadThread 1");
					tu.start();
				//	Thread tu1 = new Thread(uc1,"Upload Thread 2");
				//	tu1.start();
					System.out
					.println("spawning tracker thread to send announce messages during the interval");
					
					TrackerThread tr = new TrackerThread(frame);
					Thread tt = new Thread(tr);
					tt.run();
					
					/**
					 * System.out.println("Started Thread:++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					Thread rubtThread = new Thread(rubt);
					rubtThread.start();
					
				//	System.out.println("Out of while");
					while(!PieceStorageManager.isDownloadComplete()){ //downloadstatus = stopped
						System.out.println("Still downloading");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					 * */
				
					while(!check(frame)){
						//System.out.println("Number of pieces downloaded so far "+ PieceInformation.numberofPiecesDownloaded);
					}
					writeOutputToFile();
					if(Integer.compare(100,frame.getProgress())==0){
						JOptionPane.showMessageDialog(frame,"Download Complete : You can now play the video");
					}
					t.event = "completed";
					t.updateTracker();
					//int	totaluploaded = 0;
					//code to update the amount uploaded
				//	Scanner in = new Scanner(System.in);

						System.out.println("Connection Complete");
					
				} catch (BencodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Download file: " + downloadFileName);
			PeersUploadRecord pr = new PeersUploadRecord();
			//	ArrayList<PeersUploadRecord> uploadPeerTable = new ArrayList<PeersUploadRecord>();
			//	uploadPeerTable = pr.getUploadPeersTable();
				//	this.fp = fp; // do it in RUBTClient.java
			// at RUBTClient create PieceInfo obj
			//initialize have and request arrays
			// once we have all pieces, check for that and then do write to file there.
			System.out.println("Number of peers in uploaded list "+pr.getUploadPeersTable().size());
			System.out.println("Total Uploads so far : " + Tracker.uploaded);
		}
	
		
	}
	
	public RUBTClient(){
	
	}
	/**private static boolean check() {
			// TODO Auto-generated method stub
			for(int i = 0 ; i< PieceStorageManager.totalNumberOfPieces ;i++ )
			{
				if(PieceStorageManager.isPieceExistArray[i] != true)
					return false;
			}
				
			return  true;
		}
	 *
	public static boolean check(MainFrame frame) {
		int i;
		for (i = 0; i < PieceInformation.havepieces.length; i++) {
			frame.setProgress(PieceInformation.numberofPiecesDownloaded*100/436);
			if (PieceInformation.havepieces[i] != true) {
				return false;
			}
		}
		frame.setProgress(100);
		return true;
	} */
	/**
	 * Revised version
	 * */
	public static boolean check(MainFrame frame) {
		int i;
		for (i = 0; i < PieceStorageManager.totalNumberOfPieces; i++) {
			frame.setProgress( (PieceStorageManager.getNumberOfPiecesDownloaded())*100/436);
			if (PieceStorageManager.isPieceExistArray[i] != true) {
				return false;
			}
		}
		frame.setProgress(100);
		return true;
	} 

	/**
	 * @return the my_Peer_ID
	 */
	public static String getMy_Peer_ID() {
		return my_Peer_ID;
	}

	/**
	 * @param my_Peer_ID the my_Peer_ID to set
	 */
	public static void setMy_Peer_ID(String my_Peer_ID) {
		RUBTClient.my_Peer_ID = my_Peer_ID;
	}
	//This method starts the peer connection by creating PeerConnection object
		private static PeerConnection startPeerConnection(MainFrame frame,String ru11_ip,TorrentInfo ti,int peerPortNumber,String pid, String storeFile, Tracker t2, PieceStorageManager pi2) throws BencodingException {
			// TODO Auto-generated method stub
			
		//	PeerConnection pc = new PeerConnection("128.6.171.132",4426, ti, "-RU1103-SL#?=?P??A",storeFile);
			PeerConnection pc = new PeerConnection(frame,ru11_ip,peerPortNumber, ti, pid,storeFile,t2, pi2);
		//	PeerConnection pc = new PeerConnection("128.6.171.131",25760, ti, "-RU1103-<?D;?_??????",storeFile);
			pc.start();
			
			//	   if(pc instanceof Object)
			//	   System.out.println("Object created");
			return pc;


		}

		// Get ArrayList with list of peers
		// Pick the peer with peerID = prefixStr 
		// return it's ipAddress    
		// Each peer is a hashmap 

		public static ArrayList<PeerInfo> findPeer(ArrayList<PeerInfo> dataList) {

			String prefixStr[] = {"128.6.171.131","128.6.171.130"};//"RU11"; //128.6.171.130 and 128.6.171.131
			
			String str = null;

			ArrayList<PeerInfo> valid_Peers = new ArrayList<PeerInfo>();
			for(int index = 0; index < dataList.size();index++){
				PeerInfo pi = (PeerInfo) dataList.get(index); 
				for(int i =0; i<2;i++){
				if(pi.getIpAddress().contains(prefixStr[i])){
					System.out.println("Found it");
					valid_Peers.add(pi);
				//	PEER_PORT_NUMBER = pi.getPortNumber();
				}
				}
				
			}
				return valid_Peers; //list of valid peers provided in the assignment are found and returned
		}
		public static String findPeerTemp(ArrayList dataList) {

			String prefixStr = "RU11";
			String ipAddress = "";
			String str = null;

			for(int index = 0; index < dataList.size();index++){
				PeerInfo pi = (PeerInfo) dataList.get(index);

				//if(pi.getPeerID().contains(prefixStr)){
					if(pi.getIpAddress().equals("128.6.171.132")){
					PEER_PORT_NUMBER = pi.getPortNumber();
				}

				ipAddress = str;

				// TODO Auto-generated method stub

			}
			return ipAddress;
		}
		public static void writeOutputToFile() throws Exception
		{
			try {
				fp = new RandomAccessFile(STORE_TO_FILE,"rw");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.out.println("Writing to file");
	//		System.out.println("pieces.size:"+ PieceInformation.pieceList.size() );
		//	for(int i1=0;i1<PieceInformation.pieceList.size();i1++)
			//	fp.write(pi.getPiece(i1));
			int i;

			for (i = 0; i < PieceStorageManager.pieceList.length; i++) {
				
				byte[] array = 	PieceStorageManager.pieceList[i].array();	//piecesList[i].array();
				fp.write(array);											//fp.write(pieces[i].array());
			}
			
			fp.close();
		//	socket.close();	
		}
/*
		@Override
		public void run() {
			// TODO Auto-generated method stub
			PeerConnection c = listOfConnectedPeers.get(0);
			PeerConnection c1 = listOfConnectedPeers.get(1); 
			boolean notStopped = true;
			while(notStopped){
			Scanner in = new Scanner(System.in);
			
			boolean flag = true;
			System.out.println("Enter exit to exit system");
			System.out.println("Enter save to save and exit system");
			String inputString;
			if(!PieceStorageManager.isDownloadComplete())
				flag = true;
			while(flag){
			
				inputString  = in.nextLine();
				if(inputString.equals("exit")){
							
						c.stopPeer();
						c1.stopPeer();
						
						System.out.println("Exiting the system");
						System.exit(0);
										
				}else if(inputString.equals("save")){
					
					c.stopPeer();
					c1.stopPeer();
					System.out.println("Saving state"); // for that peer obj
					int downloadedSize = PieceStorageManager.getDownloadedSize(); //write function downloadedSize
					for(int i=0;i<PieceStorageManager.getNumberOfPiecesDownloaded();i++){
						System.out.println(PieceStorageManager.pieceList[i]);
						//Write ObjectOutputStream
					}
					
					System.exit(0);
					//downloadStatus = true;
					//break;
					//TODO : write data to file
					//flag = false;
				}else if(PieceStorageManager.isDownloadComplete()){
					flag = false;
				}
			}//end of while\
			}
		}
		private static boolean check() {
			// TODO Auto-generated method stub
			for(int i = 0 ; i< PieceStorageManager.totalNumberOfPieces ;i++ )
			{
				if(PieceStorageManager.isPieceExistArray[i] != true)
					return false;
			}
				
			return  true;
		} */
}
