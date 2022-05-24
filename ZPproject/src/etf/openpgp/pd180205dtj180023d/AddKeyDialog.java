package etf.openpgp.pd180205dtj180023d;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddKeyDialog extends Dialog {
	
	private String password = "";
	private boolean pressed = false;
	private String username = "";
	private String mail = "";
	String algorithm = "";
	private String[] algorithms = {"RSA 1024b", "RSA 2048b", "RSA 4096b"};
	
	public AddKeyDialog(Frame frame) {
		super(frame,"Add Key", true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) { 
				reset();
				setVisible(false); }
		});
		setSize(350, 350);
		setLocation(450, 100);
		setLayout(new GridLayout(4,2,10,10));
		fillScreen();
	}
	
	private void fillScreen() {
		Label l1,l2,l3;
		TextField tx1 =  new TextField();
		TextField tx2 =  new TextField();
		Choice ch = new Choice();
		Button b = new Button("Save");
		for(String item: algorithms) ch.add(item);
		add(l1 = new Label("Username:", Label.CENTER));
		add(tx1);
		add(l2 = new Label("E-mail:", Label.CENTER));
		add(tx2);
		add(l3 = new Label("Algorithm:", Label.CENTER));
		add(ch);
		add(b);
		b.addActionListener(button -> {
			if(!pressed) {
				pressed = true;
				username = tx1.getText();
				mail = tx2.getText();
				algorithm = ch.getSelectedItem();
				setLayout(new GridLayout(2,1));
				l1.setText("Password:");
				l2.setVisible(false);
				l3.setVisible(false);
				tx2.setVisible(false);
				ch.setVisible(false);
			}
			else {
				pressed = false;
				password = tx1.getText();
				generateKey();
			}
		});
	}
	
	
	private void generateKey() {
		
	}
	
	private void reset() {
		this.username = "";
		this.mail = "";
		this.algorithm = "";
		this.pressed = false;
	}

	
	
}
