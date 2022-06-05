package etf.openpgp.pd180205dtj180023d;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPUtil;

public class MyKeyRing {

	private String fileNamePublic;
	private String fileNamePrivate ;
	private String shortName;
	private PGPPublicKeyRing publicKeyRing;
	private PGPSecretKeyRing secretKeyRing;
	
	
	public MyKeyRing(PGPPublicKeyRing publicKeyRing,
			PGPSecretKeyRing secretKeyRing) {
		super();
		this.publicKeyRing = publicKeyRing;
		this.secretKeyRing = secretKeyRing;
		
		setDefaultFileName();
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
	
	private void setDefaultFileName() {
		String timestamp = publicKeyRing.getPublicKey().getCreationTime().getTime() + "";
		
		this.fileNamePrivate = "./" +
				publicKeyRing.getPublicKey().getUserIDs().next() + "_" + timestamp +"_private.asc";
		this.fileNamePublic = "./" +
				publicKeyRing.getPublicKey().getUserIDs().next()+ "_" +timestamp  + "_public.asc";
	}
	
	
	public String getFileNamePublic() {
		return fileNamePublic;
	}

	public void setFileNamePublic(String fileNamePublic) {
		this.fileNamePublic = fileNamePublic;
	}

	public String getFileNamePrivate() {
		return fileNamePrivate;
	}

	public void setFileNamePrivate(String fileNamePrivate) {
		this.fileNamePrivate = fileNamePrivate;
	}

	public void writeToFile()  {
		try {
			// public
			ArmoredOutputStream out = new ArmoredOutputStream(new FileOutputStream(getFileNamePublic()));
			publicKeyRing.encode(out);
			out.close();
			// private
			out = new ArmoredOutputStream(new FileOutputStream(getFileNamePrivate()));
			secretKeyRing.encode(out);
			out.close();
		}catch(Exception e) {
			
		}
	}
	
}
