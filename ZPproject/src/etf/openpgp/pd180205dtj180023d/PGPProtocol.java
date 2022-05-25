package etf.openpgp.pd180205dtj180023d;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import java.io.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class PGPProtocol {
    public static final int BUFFER_SIZE=1<<16;

    public enum PGPOptions {
        AUTENTICATION, ENCRYPTION, COMPRESSION, COMPATIBILITY
    }

    //add params
    public static List<PGPPublicKey> getPublicKeys() throws PGPException {
        return new ArrayList<>();

    }

    public static void encrypt(String inputFile, PGPEncryptor.SymetricKeyAlgorithm algorythm, List<PGPOptions> options){
        try{
            OutputStream output=new FileOutputStream(new File(inputFile+".pgp"));
            InputStream input=new FileInputStream(new File(inputFile));
            List<PGPPublicKey> publicKeys=getPublicKeys();

            if(options.contains(PGPOptions.COMPATIBILITY)){
                output=new Base64OutputStream(output);
            }
            if(options.contains(PGPOptions.ENCRYPTION)){
                output=PGPEncryptor.configureEncryption(algorythm,publicKeys,output);
            }
            if(options.contains(PGPOptions.COMPRESSION)){
                //compression
            }
            if(options.contains(PGPOptions.AUTENTICATION)){
                //autentication
            }

            byte[] buffer=new byte[BUFFER_SIZE];
            int size=-1;
            while ((size=input.read(buffer))!=-1){
                output.write(buffer,0,size);
            }
            output.close();

        }
        catch(Exception e){
            System.err.println(e);
        }

    }
}
