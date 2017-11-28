package src;

import java.util.ArrayList;


public class PeersUploadRecord {
	
	
	public int port;
	public String ipAddress;
	private String peerID;
	private ArrayList<PeersUploadRecord> uploadTable =  new ArrayList<PeersUploadRecord>();
	private int uploadedToPeer;
	private boolean isPeerChoked;
	private boolean isPeerRunning;
    public int uploadMax;
	
	PeersUploadRecord(){
		
	}
	
	PeersUploadRecord(int port, String ipAddress){
		
		this.port = port;
		this.ipAddress = ipAddress;
		
		isPeerChoked = false;
		isPeerRunning = false;
		
		uploadedToPeer = 0;
		
		uploadMax = 0;
		//	addPeerToUploadTable();
	}

	/**
	 * @return the isPeerRunning
	 */
	public boolean isPeerRunning() {
		return isPeerRunning;
	}

	/**
	 * @param isPeerRunning the isPeerRunning to set
	 */
	public void setPeerRunning(boolean isPeerRunning) {
		this.isPeerRunning = isPeerRunning;
	}

	/**
	 * @return the isPeerChoked
	 */
	public boolean isPeerChoked() {
		return isPeerChoked;
	}

	/**
	 * @param isPeerChoked the isPeerChoked to set
	 */
	public void setPeerChoked(boolean isPeerChoked) {
		this.isPeerChoked = isPeerChoked;
	}

	/**
	 * @return the uploadedToPeer
	 */
	public int getSizeOfDataUploadedToPeer() {
		return uploadedToPeer;
	}

	/**
	 * @param uploadedToPeer the uploadedToPeer to set
	 */
	public void setSizeOfDataUploadedToPeer(int uploadedToPeer) {
		this.uploadedToPeer = uploadedToPeer;
	}

	/**
	 * @return the uploadTable
	 */
	public ArrayList<PeersUploadRecord> getUploadPeersTable() {
		return uploadTable;
	}

	/**
	 * @param uploadTable the uploadTable to set
	 */
	public void setUploadPeersTable(ArrayList<PeersUploadRecord> uploadTable) {
		this.uploadTable = uploadTable;
	}

	public void addPeerToUploadTable(PeersUploadRecord peer){
		uploadTable.add(peer);
	}
}
