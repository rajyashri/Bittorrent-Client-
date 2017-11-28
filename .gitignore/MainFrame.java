package src;



import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
//import javax.media.*;

public class MainFrame extends JFrame implements ActionListener {
	
	private JPanel buttonPanel = new JPanel();
	private JButton peerList = new JButton("Peers");
	private JButton playVideo = new JButton("Play Torrent");
	
	private JPanel downloadPanel = new JPanel();
	private JLabel downloadTitle = new JLabel("Torrent Download Progress Bar");
	public  JProgressBar progressBar = new JProgressBar(0,100);
	
	private JPanel infoPanel = new JPanel();
	private JLabel torrentInfo = new JLabel("Torrent Info");
	private JPanel label = new JPanel();
	private JLabel fileName = new JLabel("File Name:");
	private JLabel infoHash = new JLabel("Info Hash:");
	private JLabel fileSize = new JLabel("File Size:");
	private JLabel pieceLength = new JLabel("Piece Length:");
	private JLabel trackerURL = new JLabel("Tracker URL:");
	
	private JPanel txtPanel = new JPanel();
	private JLabel fileNametxt;
	private JLabel infoHashtxt;
	private JLabel fileSizetxt;
	private JLabel pieceLengthtxt;
	private JLabel trackerURLtxt;
	private JPanel center = new JPanel();
	private JPanel resumePanel = new JPanel();
	public JButton pauseResume = new JButton("Pause ");
	ArrayList<PeerInfo> dataList;
	private String videoFile;
	
	public MainFrame(TorrentInfo ti,ArrayList<PeerInfo> dataList, String videoFile){
		this.dataList = dataList;
		this.videoFile = videoFile;
		this.setLayout(new BorderLayout(10,10));
		this.setTitle("BitTorrent Client");
		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
			//buttonPanel.add(trackerInfo);
			//trackerInfo.addActionListener(this);
			buttonPanel.add(peerList);
			peerList.addActionListener(this);
			buttonPanel.add(playVideo);
			playVideo.addActionListener(this);
		resumePanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
			resumePanel.add(downloadTitle);
			resumePanel.add(pauseResume);
			pauseResume.addActionListener(this);
		downloadPanel.setLayout(new GridLayout(2,1));
			downloadPanel.add(resumePanel);
			downloadPanel.add(progressBar);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.setVisible(true);
		label.setLayout(new GridLayout(5,1));
			label.add(fileName);
			label.add(infoHash);
			label.add(fileSize);
			label.add(pieceLength);
			label.add(trackerURL);
		txtPanel.setLayout(new GridLayout(5,1));
			fileNametxt = new JLabel(ti.file_name);
			txtPanel.add(fileNametxt);
			infoHashtxt = new JLabel(ClientHandler.byteArrayToURLString(ti.info_hash.array()));
			txtPanel.add(infoHashtxt);
			fileSizetxt = new JLabel(Integer.toString(ti.file_length));
			txtPanel.add(fileSizetxt);
			pieceLengthtxt = new JLabel(Integer.toString(ti.piece_length));
			txtPanel.add(pieceLengthtxt);
			trackerURLtxt = new JLabel(ti.announce_url.toString());
			txtPanel.add(trackerURLtxt);
		infoPanel.setLayout(new BorderLayout(10,10));
			infoPanel.add(torrentInfo,BorderLayout.NORTH);
			infoPanel.add(label,BorderLayout.WEST);
			infoPanel.add(txtPanel,BorderLayout.CENTER);
		center.setLayout(new GridLayout(2,1));
			center.add(downloadPanel);
			center.add(infoPanel);
		
		this.add(buttonPanel,BorderLayout.NORTH);
		this.add(center,BorderLayout.CENTER);
		
		
	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Pause ":
			pauseResume.setText("Resume");
			break;
		case "Resume":
			pauseResume.setText("Pause ");
			break;
		case "Peers":
			SelectPeerFrame spframe = new SelectPeerFrame(this.dataList);
			spframe.setLocationRelativeTo(this);
			spframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			spframe.pack();
			spframe.setVisible(true);
			break;
		case "Play Torrent":
			if(progressBar.getValue() != 100 ){
				JOptionPane.showMessageDialog(this,"File not yet downloaded : please wait");
				break;
			}
			else{
				try {
					Desktop.getDesktop().open(new File(this.videoFile));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
				/*File file =new File(this.videoFile);
				try {
					Player mediaplayer = Manager.createRealizedPlayer(file.toURI().toURL());
					Component video = mediaplayer.getVisualComponent();
					Component controls = mediaplayer.getControlPanelComponent();
					JFrame videoframe = new JFrame("Media Player");
					videoframe.add(video,BorderLayout.CENTER);
					videoframe.add(controls,BorderLayout.SOUTH);
					videoframe.setLocationRelativeTo(this);
					videoframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					videoframe.pack();
					videoframe.setVisible(true);
				} catch (NoPlayerException | CannotRealizeException	| IOException e1) {
					e1.printStackTrace();
				}
				JFrame mediaPlayer = new JFrame("Media Player");
				mediaPlayer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
			}*/
			break;
		}
	}
	public void setProgress(int value){
		progressBar.setValue(value);
	}
	public int getProgress(){
		return progressBar.getValue();
	}
	public String getButtonString(){
		return pauseResume.getActionCommand();
	}
	public void setArrayList(ArrayList<PeerInfo> datalist){
		this.dataList = datalist;
	}
}