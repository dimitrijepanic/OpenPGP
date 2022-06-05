package etf.openpgp.pd180205dtj180023d;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DeleteKeyDialog extends Dialog {

	private AppMainFrame mainFrame;
	
	// deleted row 
	private int id = -1;
	
	// labels
	Label l1 = new Label();
	Label l2 = new Label();
	Label l3 = new Label();
	Label l4 = new Label("\t\t\t\t\t\t");
	Button b1 = new Button("Delete");
	
	public DeleteKeyDialog(Frame frame) {
		super(frame, "Delete Key", true);
		mainFrame = (AppMainFrame) frame;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});
		
		setSize(230, 200);
		setLocation(550, 200);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(l1);
		add(l2);
		add(l3);
		add(l4);
		add(b1);
		b1.addActionListener(b->{
			mainFrame.removeRow(id);
			setVisible(false);
		});
	}
	
	private void fillScreen(String keyId, String userId, String timestamp) {
		
		l1.setText("\t\t\t\t\t   Key Id :  "+ keyId);
		l2.setText("\t\t\t\t\t   User Id : "+ userId);
		l3.setText("\t\t\t\t\t   Time :    "+ timestamp);
		l4.setText("\t\t\t\t\t\t");
	}
	public void setValues(MyKeyRing keyRing, int id) {
		this.id = id;
		String keyId = keyRing.getPublicKeyRing().getPublicKey().getKeyID() + "";
		String userId = keyRing.getPublicKeyRing().getPublicKey().getUserIDs().next();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
		String timestamp = dateFormat.format(keyRing.getPublicKeyRing().getPublicKey().getCreationTime());
		
		fillScreen(keyId, userId, timestamp);
	}
	
	
	
}
