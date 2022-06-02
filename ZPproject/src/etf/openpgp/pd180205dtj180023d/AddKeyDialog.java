package etf.openpgp.pd180205dtj180023d;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;

// dialog from creating the key
public class AddKeyDialog extends Dialog {

	// parent Frame
	AppMainFrame mainFrame;
	
	// error String
	private static String errorString = "              All fields required.            ";

	// user info
	private String password = "";
	private boolean pressed = false;
	private String username = "";
	private String mail = "";
	String algorithm = "";
	
	//labels and text fields
	Label l1, l2, l3, l4;
	TextField tx1 = new TextField(10);
	TextField tx2 = new TextField(10);
	Choice ch = new Choice();
	
	// master key for user - used to sign the subkey
	private Map<String, AsymmetricCipherKeyPair> masterKeyMap = new HashMap<>();

	// values for RSA - certainty
	// BigInteger default values is 65537, removed from library
	private static String[] algorithms = { "RSA 1024b", "RSA 2048b", "RSA 4096b" };
	private static int certainty = 30;
	private static BigInteger publicExponent = new BigInteger("65537");

	// things that the same for all key generators
	private static BcPBESecretKeyEncryptorBuilder encryptor = 
			new BcPBESecretKeyEncryptorBuilder(0x0d);
	public AddKeyDialog(Frame frame) {
		super(frame, "Add Key", true);
		this.mainFrame = (AppMainFrame) frame;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeWindow();
			}
		});
		setSize(250, 180);
		setLocation(550, 200);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		fillScreen();
	}

	private void fillScreen() {
		// all elements
		
		Button b = new Button("Save");
		// fil choice
		for (String item : algorithms)
			ch.add(item);

		l1 = new Label("Username: ", Label.CENTER);
		add(l1);
		add(tx1);
		l2 = new Label("Email:\t\t   ", Label.CENTER);
		add(l2);
		add(tx2);
		add(l3 = new Label("Algorithm:   ", Label.CENTER));
		add(ch);
		add(new Label("\n"));
		add(new Label("\t\t\t\t\t\t"));
		b.setFocusable(true);
		add(b, new GridBagConstraints());
		add(new Label("\t\t\t\t\t\t"));

		// error label set
		l4 = new Label("                                                          ", Label.CENTER);
		l4.setForeground(Color.RED);
		// l4.setVisible(true);
		add(l4);

		b.addActionListener(button -> {
			if (!pressed) {
				setLabel(l4, "                                         ");
				pressed = true;
				username = tx1.getText();
				mail = tx2.getText();
				algorithm = ch.getSelectedItem();

				if ((this.username == null) || ("".equals(this.username)) || this.mail == null || "".equals(this.mail)
						|| this.algorithm == null || "".equals(this.algorithm)) {
					setLabel(l4, errorString);
					pressed = false;
					return;
				}

				setLayout(new FlowLayout(FlowLayout.LEFT));
				l1.setText("Password:");
				tx1.setText("");
				l2.setVisible(false);
				l3.setVisible(false);
				tx2.setVisible(false);
				ch.setVisible(false);
			} else {
				pressed = false;
				password = tx1.getText();
				setLabel(l4, "                                         ");

				if (this.password == null || "".equals(this.password)) {
					setLabel(l4, errorString);
					pressed = true;
					return;
				}

				generateKey();
				dispose();
				clear();
			}
		});
	}
	
	private void clear() {
		tx1.setText("");
		tx2.setText("");
		l2.setVisible(true);
		l3.setVisible(true);
		tx2.setVisible(true);
		ch.setVisible(true);
		l1.setText("Username: ");
	}

	private void setLabel(Label l, String text) {
		l.setText(text);
	}

	private void generateKey() {

		// sredi ovo znas kako sve ok!
		RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
		generator.init(getRSAParams(getKeySize()));

		// za potpisivanje kljuceva
		PGPKeyPair masterKeyPair = getMasterKeyPair(this.username, this.algorithm, generator);

		AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
		
		PGPKeyPair subkeyPair = null;
		try {
			subkeyPair = new BcPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());
		} catch (PGPException e) {
			e.printStackTrace();
		}

		// how to encrypt the secret key
		// izdvoji kao static polje
		PBESecretKeyEncryptor pske = encryptor
				.build(this.password.toCharArray());

		// 0x13 certification forwarding
		PGPKeyRingGenerator keyRingGen = null;
		try {
			keyRingGen = new PGPKeyRingGenerator(
					0x13, 
					masterKeyPair, 
					this.mail,
					null, 
					null, 
					null,
					new BcPGPContentSignerBuilder(masterKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
					pske);
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			keyRingGen.addSubKey(subkeyPair);
		} catch (PGPException e) {
			e.printStackTrace();
		}

		PGPPublicKeyRing publicKeyRing = keyRingGen.generatePublicKeyRing();
		PGPSecretKeyRing secretKeyRing = keyRingGen.generateSecretKeyRing();
		MyKeyRing myKeyRing = new MyKeyRing("./pana.asc",publicKeyRing,secretKeyRing) ;
		mainFrame.addKeyRing(myKeyRing);
		myKeyRing.writeToFile();
		
		// setuj novi kljuc za potpisivanje
		setNewSignKey(this.username, this.algorithm, keyPair);
		closeWindow();

	}
	
	private void setNewSignKey(String username,String algorithm, AsymmetricCipherKeyPair pair) {
		String key = username + " " + algorithm;
		masterKeyMap.put(key, pair);
	}

	private void closeWindow() {
		reset();
		clear();
		setVisible(false);
	}

	// generisi master kljuc ako ne postoji
	private PGPKeyPair getMasterKeyPair(String username, String algorithm, RSAKeyPairGenerator generator) {
		String key = username + " " + algorithm;


		AsymmetricCipherKeyPair pair = null;
		if (masterKeyMap.containsKey(key)) {
			 pair = masterKeyMap.get(key);
			 
		}
		
		if(pair == null) pair = generator.generateKeyPair();
		PGPKeyPair keyPair = null;
		try {
			keyPair = new BcPGPKeyPair(PGPPublicKey.RSA_SIGN, pair, new Date());
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		masterKeyMap.put(key, pair);

		return keyPair;
	}

	// certainty - verovatnoca da bude prime number - kao kod mille rabina
	// exponent je n
	// strength je broj bita na koliko se generise
	// secure random sluzi da se zastiti privatna vrednost kljuca da se ne bi videla
	private RSAKeyGenerationParameters getRSAParams(int strength) {
		return new RSAKeyGenerationParameters(publicExponent, new SecureRandom(), strength, certainty);
	}

	private int getKeySize() {
		switch (this.algorithm) {
		case "RSA 1024b":
			return 1024;
		case "RSA 2048b":
			return 2048;
		case "RSA 4096b":
			return 4096;
		}

		return -1;
	}

	private void reset() {
		this.username = "";
		this.mail = "";
		this.algorithm = "";
		this.pressed = false;
	}

}
