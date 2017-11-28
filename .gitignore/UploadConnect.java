package src;



import java.io.IOException;
import java.net.*;
import java.util.Scanner;


public class UploadConnect implements Runnable {
	ServerSocket server = null;
	Socket client = null;
	private int port ;// = 6884;
	MainFrame frame;
/**	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	*/
	TorrentInfo ti;
	PieceStorageManager pm;
	String valid_ip;
	
	public UploadConnect(MainFrame frame,TorrentInfo ti, PieceStorageManager pm, int port, String valid_ip){
		
		this.frame = frame;
		this.port = port;
		this.valid_ip = valid_ip;
		
		try{
			server = new ServerSocket(port);
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		this.ti = ti;
		this.pm = pm;
	}

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		int choice = 0;
		//choice = in.nextInt() ;
		
		while(true){
			
			try {
				
				client = server.accept();
				System.out.println("Server connection established");
				
		
			
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
		//		if((client.getInetAddress().getHostAddress()).equals(valid_ip)){
					System.out.println("*********************Connected to valid upload peer " + client.getInetAddress());
					PeersUploadRecord newPeer = new PeersUploadRecord(client.getPort(),client.getInetAddress().getHostAddress());
		

				       UploadPeer peer = new UploadPeer(frame,client,ti,pm,newPeer);
				Thread tp = new Thread(peer);
				tp.start();
		/**		break;
				}else {
					System.out.println("Closing invalid clients" + client.getInetAddress().getHostAddress());
					try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
				}*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
		
}
