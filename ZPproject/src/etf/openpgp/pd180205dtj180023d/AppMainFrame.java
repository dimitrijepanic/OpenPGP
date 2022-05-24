package etf.openpgp.pd180205dtj180023d;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppMainFrame extends Frame implements ActionListener{
	
	private AddKeyDialog dialog;
	private EncryptionDialog encryptionDialog;
	
	class WindowClosingAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			dispose();
		}
	}
	
	public AppMainFrame() {
		super("Projekat ZP");
		addWindowListener(new WindowClosingAdapter());
		setSize(600, 600);
		setLocation(400, 50);
		setLayout(new GridLayout());
		fillScreen();
		setVisible(true);
	}
	
	private void fillScreen() {	
		Panel panel = new Panel();
		Button b1 = new Button("Add New Key");
		b1.addActionListener(b->{
			if(dialog == null) dialog = new AddKeyDialog(this);
			dialog.setVisible(true);
		});
		configureMenu();
		b1.setFont(new Font("Serif", Font.BOLD, 15));
		panel.add(b1);
		add(panel);
	}

	private void configureMenu(){
		MenuBar menuBar=new MenuBar();
		Menu menu=new Menu();
		MenuItem encryptionItem=new MenuItem();
		encryptionItem.setLabel("Encrypt");
		menu.add(encryptionItem);
		menu.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.addActionListener(item->{
			if(encryptionDialog==null) encryptionDialog=new EncryptionDialog(this);
			encryptionDialog.setVisible(true);
		});
		menu.setLabel("Options");
		menuBar.add(menu);
		setMenuBar(menuBar);
	}
	
	public void actionPerformed(ActionEvent ae) {

	}
}
