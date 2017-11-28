package src;


public class TrackerThread extends RUBTClient implements Runnable {
	boolean firstTime = true;
	MainFrame frame;
	public TrackerThread(MainFrame frame){
		this.frame = frame;
	}
	public void run() {
		System.out.println("NUM of pieces Downoaded " + PieceInformation.numberofPiecesDownloaded );
		data = 	t.updateTracker();
		frame.setArrayList(data);
		try {
			Thread.sleep(t.time_interval);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
