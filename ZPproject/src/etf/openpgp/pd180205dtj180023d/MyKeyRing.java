package etf.openpgp.pd180205dtj180023d;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPUtil;

public class MyKeyRing {

	private String fileName;
	private String shortName;
	private PGPPublicKeyRing publicKeyRing;
	private PGPSecretKeyRing secretKeyRing;
	
	
	public MyKeyRing(String fileName, PGPPublicKeyRing publicKeyRing,
			PGPSecretKeyRing secretKeyRing) {
		super();
		this.fileName = fileName;
		this.publicKeyRing = publicKeyRing;
		this.secretKeyRing = secretKeyRing;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public PGPPublicKeyRing getPublicKeyRing() {
		return publicKeyRing;
	}
	public void setPublicKeyRing(PGPPublicKeyRing publicKeyRing) {
		this.publicKeyRing = publicKeyRing;
	}
	public PGPSecretKeyRing getSecretKeyRing() {
		return secretKeyRing;
	}
	public void setSecretKeyRing(PGPSecretKeyRing secretKeyRing) {
		this.secretKeyRing = secretKeyRing;
	}
	
	public void writeToFile()  {
		// samo public za sad da vidim da l ce mi ga lepo ispsiati 
		try {
			ArmoredOutputStream out = new ArmoredOutputStream(new FileOutputStream(fileName));
			publicKeyRing.encode(out);
//			ArmoredInputStream in = new ArmoredInputStream(new FileInputStream(fileName));
//			byte[] bytes = new byte[100000];
//			bytes = in.readAllBytes();
//			System.out.println(new String(bytes));
//			InputStream in = PGPUtil.getDecoderStream(new FileInputStream(fileName));
//			byte[] bytes = in.readAllBytes();
//			System.out.println(new String(bytes));
		}catch(Exception e) {
			
		}
	}
	
}
