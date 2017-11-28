package src;



import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;



public class Tracker extends Thread {
	
	
	private String peerId;
	private TorrentInfo ti;
	private String info_hash;
	private byte[] trackerInfo;
	private ArrayList<PeerInfo> peerList;
	public int time_interval;
	public static int uploaded;
	public static int downloaded;
	public static int portNumber;
	public URL req ; 
	public String event; 
	public int left;
	
	public Tracker(TorrentInfo ti,String peerId,int listenport, String info_hash){
		this.peerId = peerId;
		this.ti = ti;
		this.info_hash = info_hash;
		portNumber = 6884; //listenport; //6884;/
//		this.uploaded = 0;
	//	this.downloaded = 0;
		this.left = ti.file_length ;
		req = createURL(ti.announce_url);
//		trackerInfo = setupConnection(this.ti,this.peerId,this.info_hash);
		event = null;
		
	}
	public URL createURL(URL announceURL) {
	//	downloaded = PieceInformation.numberofPiecesDownloaded;
		//uploaded = PieceInformation.numberofPiecesUploaded;
		String newURL = announceURL.toString();
		
		newURL +=  "?" + "info_hash"+"="+info_hash+"&"+"peer_id"+"="+peerId+"&" + "port"+"="+ 6884+"&"+"uploaded"+"="+ uploaded +"&"+"downloaded"+"="+ downloaded+"&"+"left"+"="+left ;
		if (this.event != null)
			newURL += "&event=" + this.event;
		System.out.println("EVENT:" + this.event);
		try {
			return new URL(newURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
			return null;
		}

	}
	public int getDownloaded(){
		return this.downloaded;
	}
	public void setDownloaded (int fileLength){
		this.downloaded = fileLength;
	}
	public void setUploaded(int uploaded){
		this.uploaded = uploaded;
	}
	public int getUploaded(){
		return this.uploaded;
	}
	/**
	 * @return the peerList
	 */
	public ArrayList<PeerInfo> getPeerList(byte[] getTrackerResponse) {
		Object o = null;
		try {
			o = Bencoder2.decode(getTrackerResponse);
		} catch (BencodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PeerInfo pi = new PeerInfo();
			ArrayList<PeerInfo> peerList = pi.getPeer(o);
		 ToolKit.print(o);
		 
		return peerList;
	}


	/**
	 * @param peerList the peerList to set
	 */
	public void setPeerList(ArrayList<PeerInfo> peerList) {
		this.peerList = peerList;
	}


	/**
	 * @return the peerId
	 */
	public String getPeerId() {
		return peerId;
	}


	/**
	 * @param peerId the peerId to set
	 */
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}


	/**
	 * @return the ti
	 */
	public TorrentInfo getTi() {
		return ti;
	}


	/**
	 * @param ti the ti to set
	 */
	public void setTi(TorrentInfo ti) {
		this.ti = ti;
	}


	/**
	 * @return the info_hash
	 */
	public String getInfo_hash() {
		return info_hash;
	}


	/**
	 * @param info_hash the info_hash to set
	 */
	public void setInfo_hash(String info_hash) {
		this.info_hash = info_hash;
	}


	/**
	 * @return the trackerInfo
	 */
	public byte[] getTrackerInfo() {
		return trackerInfo;
	}


	/**
	 * @param trackerInfo the trackerInfo to set
	 */
	public void setTrackerInfo(byte[] trackerInfo) {
		this.trackerInfo = trackerInfo;
	}


	public byte[] setupConnection(TorrentInfo ti, String peerId, String info_hash){//TorrentInfo ti, String pid, String info_hash){

		
		//Upload u = new Upload();
		//String uploaded =; 
	//	int portNumber = 6884;//u.getListenPort(); //u.pickPortNumber(); 	//
	//	String portNum = Integer.toString(portNumber);
	//	System.out.println("value of portNum:"+portNumber);
	//	String urlNew = ti.announce_url + "?" + "info_hash"+"="+info_hash+"&"+"peer_id"+"="+peerId+"&" + "port"+"="+ ti.announce_url.getPort()+"&"+"uploaded"+"="+ 0 +"&"+"downloaded"+"="+ 0 +"&"+"left"+"="+ti.file_length ; 
		//String urlNew = ti.announce_url + "?" + "info_hash"+"="+info_hash+"&"+"peer_id"+"="+peerId+"&" + "port"+"="+ portNumber+"&"+"uploaded"+"="+ uploaded +"&"+"downloaded"+"="+ downloaded +"&"+"left"+"="+(ti.file_length - downloaded) ; 


		HttpURLConnection hc = null;
		try {
			hc = (HttpURLConnection) this.req.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not open connection");
		}

		byte[] retArray = null ;
		try {
			DataInputStream dis = new DataInputStream(hc.getInputStream());
			int dataSize = hc.getContentLength();
			retArray = new byte[dataSize];
			dis.readFully(retArray);
			dis.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retArray;
	}

	public ArrayList<PeerInfo> updateTracker(){
		
		byte[] responseFromTracker = null;
		
		this.req = createURL(ti.announce_url);
		responseFromTracker = setupConnection(ti, peerId, info_hash);
		Object o = null;
		try{
			o = Bencoder2.decode(responseFromTracker);
		}catch (BencodingException e){
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		HashMap<ByteBuffer, Object> response = (HashMap<ByteBuffer, Object>) o;

		ByteBuffer min_interval = ByteBuffer.wrap("min interval".getBytes());
		
		if(response.containsKey(min_interval)){
			this.time_interval = (Integer)response.get(min_interval);
		}
		else{
			this.time_interval = 120;
		}
		 ArrayList<PeerInfo> peerList = new ArrayList<PeerInfo>();
		 peerList =  getPeerList(responseFromTracker);
		
		ToolKit.print(o);	
		return peerList;
	}
	
}
