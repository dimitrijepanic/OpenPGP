package etf.openpgp.pd180205dtj180023d;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;


public class AppMainFrame extends Frame implements ActionListener{
	
	// dialogs
	private AddKeyDialog dialog;
	private EncryptionDialog encryptionDialog;
	private DeleteExportKeyDialog deleteKeyDialog;
	private List<MyKeyRing> keyRings = new ArrayList<>();
	private ImportKeyDialog importKeyDialog ;
	private DecryptionDialog decryptionDialog;
	
	// panels for keys
	private Panel keyPanel;
	private JScrollPane p1;
	private JTable table;
	private JTable privateKeyTable;
	
	// press count
	private int pressedCount = 0;
	class WindowClosingAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			saveKeys();
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
		deleteKeyDialog = new DeleteExportKeyDialog(this);
		try {
			loadKeys();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// problem sa kljucevima
		}
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
		privateKeyTable = new JTable();
		addTableMouseListener();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] {"userId", "keyId", "timestamp"});
		DefaultTableModel model2 = new DefaultTableModel(new Object[][] {}, new String[] {"userId", "keyId", "timestamp"});
		table.setModel(model);
		privateKeyTable.setModel(model2);
		addTableMouseListener();
		p1 = new JScrollPane(table);
		p1.setPreferredSize(new Dimension(400, 250));
		keyPanel.add(p1);
		JScrollPane p2 = new JScrollPane(privateKeyTable);
		p2.setPreferredSize(new Dimension(400, 250));
		keyPanel.add(p2);
		add(keyPanel, BorderLayout.CENTER);
	}
	
	private void saveKeys() {
		int numOfKeys = keyRings.size();
		String setupFile = "./savedkeys/setup.txt";
		try {
			FileWriter out = new FileWriter(setupFile);
			out.write(numOfKeys + "");
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		for(int i = 0; i < keyRings.size(); i++) {
			String fileName = "./savedkeys/" + i ;
			keyRings.get(i).saveKey(fileName);
		}
	}
	
	private void loadKeys() throws Exception{
		int numOfKeys;
		
		FileReader in = new FileReader("./savedkeys/setup.txt");
		numOfKeys = in.read();
		in.close();
		
		for(int i = 0; i < numOfKeys; i++) {
			ArmoredInputStream ain = new ArmoredInputStream(new FileInputStream("./savedkeys/"+i + "_public.asc"));
			PGPPublicKeyRing pk = new BcPGPPublicKeyRing(ain);
			ain.close();
			ain = new ArmoredInputStream(new FileInputStream("./savedkeys/"+i + "_private.asc"));
			PGPSecretKeyRing pk2 = new BcPGPSecretKeyRing(ain);
			ain.close();
			addKeyRing(new MyKeyRing(pk, pk2));
		}
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
		
		privateKeyTable.addMouseListener(new MouseListener() {
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
					deleteKeyDialog.setValues(keyRings.get(privateKeyTable.getSelectedRow()), privateKeyTable.getSelectedRow());
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
		MenuItem decryptionItem=new MenuItem();
		decryptionItem.setLabel("Decrypt");
		MenuItem dialogItem = new MenuItem();
		dialogItem.setLabel("Add Key");
		MenuItem importItem = new MenuItem();
		importItem.setLabel("Import Key");
		menu.add(dialogItem);
		menu.add(decryptionItem);
		menu.add(encryptionItem);
		menu.add(importItem);
		menu.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.setFont(new Font("Serif", Font.BOLD, 15));
		encryptionItem.addActionListener(item->{
			if(encryptionDialog==null) encryptionDialog=new EncryptionDialog(this);
			encryptionDialog.setVisible(true);
		});
		decryptionItem.setFont(new Font("Serif", Font.BOLD, 15));
		decryptionItem.addActionListener(item->{
//			PGPProtocol.decrypt("D:\\GIT_projekti\\ZPproject\\proba2.txt_encrypted.pgp","jana", keyRings);
			if(decryptionDialog==null) decryptionDialog=new DecryptionDialog(this);
			decryptionDialog.setVisible(true);
		});
		
		dialogItem.addActionListener(b->{
			if(dialog == null) dialog = new AddKeyDialog(this);
			dialog.setVisible(true);
		});
		
		importItem.addActionListener(item->{
			if(importKeyDialog==null) importKeyDialog=new ImportKeyDialog(this);
			importKeyDialog.setAll();
			importKeyDialog.setVisible(true);
		});
		menu.setLabel("Options");
		menuBar.add(menu);
		setMenuBar(menuBar);
	}
	
	
	public void addKeyRing(MyKeyRing keyRing) {
		keyRings.add(keyRing);
		DefaultTableModel model = (DefaultTableModel) (table.getModel());
		model.addRow(Util.generateTableRow(keyRing));
		DefaultTableModel model2 = (DefaultTableModel) (privateKeyTable.getModel());
		model2.addRow(Util.generateTableRow(keyRing));
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeRow(int i) {
		if(i >= 0 && i < table.getRowCount()) {
			DefaultTableModel model = (DefaultTableModel) (table.getModel());
			DefaultTableModel model2 = (DefaultTableModel) (privateKeyTable.getModel());
			model.removeRow(i);
			model2.removeRow(i);
			keyRings.remove(i);
		}

	}
}
