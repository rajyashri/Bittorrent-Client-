package src;



import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class PeerFrame extends JFrame implements ActionListener {
	
	private JButton close = new JButton("Close");
	private JPanel label = new JPanel();
	private JLabel peerID = new JLabel("PeerID");
	private JLabel portNumber = new JLabel("Port Number");
	private JLabel ipAddress = new JLabel("IP Address");
	private JPanel labeltxt = new JPanel();
	private JLabel peerIDtxt;
	private JLabel portNumbertxt;
	private JLabel ipAddresstxt;
	private JPanel buttonPanel = new JPanel();
	private JPanel center = new JPanel();
	
	public PeerFrame(String peerid,String ipaddress,int portnumber){
		this.setLayout(new BorderLayout(10,10));
		this.setTitle("Peer Info");
		
		label.setLayout(new GridLayout(3,1));
			label.add(peerID);
			label.add(portNumber);
			label.add(ipAddress);
		labeltxt.setLayout(new GridLayout(3,1));
			peerIDtxt = new JLabel(peerid);
			labeltxt.add(peerIDtxt);
			portNumbertxt = new JLabel(Integer.toString(portnumber));
			labeltxt.add(portNumbertxt);
			ipAddresstxt = new JLabel(ipaddress);
			labeltxt.add(ipAddresstxt);
		center.setLayout(new BorderLayout(10,10));
			center.add(label,BorderLayout.WEST);
			center.add(labeltxt,BorderLayout.CENTER);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
			buttonPanel.add(close);
			close.addActionListener(this);
		this.add(center,BorderLayout.CENTER);
		this.add(buttonPanel,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Close":
			this.dispose();
			break;
		}
		
	}
}
