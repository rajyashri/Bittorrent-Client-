package src;



import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class SelectPeerFrame extends JFrame implements ActionListener {
	private JList peerIDList;
	private JButton select = new JButton("Select");
	private JButton cancel = new JButton("Cancel");
	private JPanel buttons = new JPanel();
	ArrayList<PeerInfo> dataList;
	
	public SelectPeerFrame(ArrayList<PeerInfo> dataList){ 
		this.dataList = dataList;
		this.setLayout(new BorderLayout(10,10));
		this.setTitle("List of Peers");
		ArrayList<String> peerList = new ArrayList<String>();
		for(int i = 0;i<dataList.size();i++){//populate JList
			PeerInfo pi = (PeerInfo) dataList.get(i);
			peerList.add(pi.getPeerID());
		}
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
			buttons.add(select);
			select.addActionListener(this);
			buttons.add(cancel);
			cancel.addActionListener(this);
		peerIDList = new JList(peerList.toArray());
			peerIDList.setVisibleRowCount(5);
			peerIDList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane peers = new JScrollPane(peerIDList);
		this.add(peers,BorderLayout.CENTER);
		this.add(buttons,BorderLayout.SOUTH);
			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Cancel":
			this.dispose();
			break;
		case "Select":
			if(((String)peerIDList.getSelectedValue() == null)){
				break;
			}
			this.getInfo();
			this.dispose();
			break;
		}
	}
	public void getInfo(){
		String peerId = (String)peerIDList.getSelectedValue();
		for(int i = 0;i<dataList.size();i++){
			PeerInfo pi = (PeerInfo) dataList.get(i);
			if((peerId).equals(pi.getPeerID())){
				PeerFrame pframe = new PeerFrame(pi.getPeerID(),pi.getIpAddress(),pi.getPortNumber());
				pframe.setLocationRelativeTo(null);
				pframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				pframe.pack();
				pframe.setVisible(true);
				return;
			}
		}
		return;
	}
}

