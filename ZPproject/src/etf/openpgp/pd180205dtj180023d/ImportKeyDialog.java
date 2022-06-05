package etf.openpgp.pd180205dtj180023d;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JLabel;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;

public class ImportKeyDialog extends Dialog{

	private AppMainFrame mainFrame;
	
	 private MyKeyRing keyRing;
	// buttons 
	private Button buttonPublic = new Button("Add Public Key");
	private Button buttonPrivate = new Button("Add Private Key");
	private Button buttonSave = new Button("Save");
	
	// labels
	private Label l1 = new Label(" No Key Chosen");
	private Label l2 = new Label(" No Key Chosen");
	
	//files
	private String filePublic;
	private String filePrivate;
	
	public ImportKeyDialog(Frame frame) {
		super(frame, "Import Key", true);
		mainFrame = (AppMainFrame) frame;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});
		
		setSize(230, 150);
		setLocation(550, 200);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		l1.setForeground(Color.RED);
		l2.setForeground(Color.RED);
		add(buttonPublic);
		add(l1);
		add(buttonPrivate);
		add(l2);
		add(new Label("\t\t\t\t\t\n"));
		add(buttonSave);
		
		buttonPublic.addActionListener(b ->{
			FileDialog dialog = new FileDialog(mainFrame, "Choose destination", FileDialog.LOAD);
		    dialog.setVisible(true);
		    // dodaj ako je tekst predugacak
		    l1.setForeground(Color.GREEN);
		    l1.setText(dialog.getFile());
		    filePublic = dialog.getDirectory() + dialog.getFile();
		});
		
		buttonPrivate.addActionListener(b ->{
			FileDialog dialog = new FileDialog(mainFrame, "Choose destination", FileDialog.LOAD);
		    dialog.setVisible(true);
		    // dodaj ako je tekst predugacak
		    l2.setForeground(Color.GREEN);
		    l2.setText(dialog.getFile());
		    filePrivate = dialog.getDirectory() + dialog.getFile();
		});

		buttonSave.addActionListener(b->{
			try {
				ArmoredInputStream in = new ArmoredInputStream(new FileInputStream(this.filePublic));
				PGPPublicKeyRing pk = new BcPGPPublicKeyRing(in);
				in.close();
				in = new ArmoredInputStream(new FileInputStream(this.filePrivate));
				PGPSecretKeyRing pk2 = new BcPGPSecretKeyRing(in);
				in.close();
				keyRing = new MyKeyRing(pk, pk2);
				addKey(keyRing);
				setVisible(false);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PGPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
	}
	
	private void addKey(MyKeyRing keyRing) {
		mainFrame.addKeyRing(keyRing);
	}
	
	public void setAll() {
		l1.setForeground(Color.RED);
		l2.setForeground(Color.RED);
		l1.setText("No Key Chosen");
		l2.setText("No Key Chosen");
		filePublic = "";
		filePrivate = "";
	}
	
}
