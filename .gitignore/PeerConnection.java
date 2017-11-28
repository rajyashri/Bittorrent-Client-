package src;


/*

 * CS 352 : Group 18 : Phase - 1
 * 
 * Authors : Rajyashri Vasudevamoorthy
 * 			 Daniel Hidalgo
 * 			 Isaac Manayath
 * */

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;


public class PeerConnection extends Thread{

	public String PEER_INFO_HASH;
	RandomAccessFile fp;
	//ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	Socket s;
	DataInputStream isr;
	DataOutputStream ps;
	TorrentInfo ti;
	byte id=0;
	int length=0;
	Tracker t;
	DownloaderPeer downloadPeer;
	PieceStorageManager pm;
	MainFrame frame;
	ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	byte[] fullpiece;
	
	
	public void openThePeerSocket(String ip_addr,int portNum) throws Exception
	{
		s = new Socket(ip_addr,portNum);
		if(s.isConnected())
			System.out.println("========Socket Connected");
		
		//SETUP INPUT OUTPUT STREAMS TO THE SOCKET
		
		 isr = new DataInputStream(s.getInputStream());
		 ps = new DataOutputStream(s.getOutputStream());
	}
	
	public PeerConnection(MainFrame frame,String ip_addr,int portNum,TorrentInfo ti,String pid, String storeFile, Tracker t2, PieceStorageManager pm ) throws BencodingException{
				this.frame = frame;
				this.ti=ti;
				this.t = t2;
				this.pm = pm;
				
				try {
					openThePeerSocket(ip_addr,portNum);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			

	}
	
	public void run()
	{
		System.out.println("Running in peerconnection run");
		try {
		
			downloadPeer = new DownloaderPeer(frame,isr,ps,s, ti,null, fp,pm);
		
		Thread tt = new Thread(downloadPeer);
		tt.start();

		t.event = "started";
		t.updateTracker();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
		public void stopPeer(){
			
			this.downloadPeer.stop();
		}
	
}




