package etf.openpgp.pd180205dtj180023d;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.bouncycastle.openpgp.PGPPublicKey;


public class AppMainFrame extends Frame implements ActionListener{
	
	private AddKeyDialog dialog;
	private EncryptionDialog encryptionDialog;
	private DeleteKeyDialog deleteKeyDialog;
	private List<MyKeyRing> keyRings = new ArrayList<>();
	
	// panels for keys
	private Panel keyPanel;
	private JScrollPane p1;
	private JTable table;
	
	// press count
	private int pressedCount = 0;
	class WindowClosingAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			dispose();
		}
	}

	public List<MyKeyRing> getKeyRings(){
		return keyRings;
	}

	public AppMainFrame() {
		super("Projekat ZP");
		addWindowListener(new WindowClosingAdapter());
		setSize(600, 600);
		setLocation(400, 50);
		setLayout(new BorderLayout());
		fillScreen();
		deleteKeyDialog = new DeleteKeyDialog(this);
		setVisible(true);
	}
	
	private void fillScreen() {	
		Panel panel = new Panel();
		//Button b1 = new Button("Add New Key");
		
		configureMenu();
		//b1.setFont(new Font("Serif", Font.BOLD, 15));
		//panel.add(b1);
		add(panel, BorderLayout.PAGE_START);
		
		keyPanel = new Panel();
		table = new JTable();
		addTableMouseListener();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] {"userId", "keyId", "timestamp"});
		table.setModel(model);
		addTableMouseListener();
		p1 = new JScrollPane(table);
		keyPanel.add(p1);
		add(keyPanel, BorderLayout.CENTER);
	}
	
	private void addTableMouseListener() {
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				pressedCount ++;
				
				if(pressedCount == 2) {
					pressedCount = 0; 
					return;
				}
				
				if(pressedCount == 1) {
					deleteKeyDialog.setValues(keyRings.get(table.getSelectedRow()), table.getSelectedRow());
					deleteKeyDialog.setVisible(true);
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void configureMenu(){
		MenuBar menuBar=new MenuBar();
		Menu menu=new Menu();
		MenuItem encryptionItem=new MenuItem();
		encryptionItem.setLabel("Encrypt");
		MenuItem dialogItem = new MenuItem();
		dialogItem.setLabel("Add Key");
		menu.add(dialogItem);
		menu.add(encryptionItem);
		menu.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.addActionListener(item->{
			if(encryptionDialog==null) encryptionDialog=new EncryptionDialog(this);
			encryptionDialog.setVisible(true);
		});
		dialogItem.addActionListener(b->{
			if(dialog == null) dialog = new AddKeyDialog(this);
			dialog.setVisible(true);
		});
		menu.setLabel("Options");
		menuBar.add(menu);
		setMenuBar(menuBar);
	}
	
	
	public void addKeyRing(MyKeyRing keyRing) {
		keyRings.add(keyRing);
		DefaultTableModel model = (DefaultTableModel) (table.getModel());
		model.addRow(Util.generateTableRow(keyRing));
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeRow(int i) {
		if(i >= 0 && i < table.getRowCount()) {
			DefaultTableModel model = (DefaultTableModel) (table.getModel());
			model.removeRow(0);
			keyRings.remove(i);
		}

	}
}
