package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.openpgp.PGPPublicKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.List;

public class PGPProtocol {
    public static final int BUFFER_SIZE=1<<16;

    public enum PGPOptions {
        AUTENTICATION, ENCRYPTION, COMPRESSION, COMPATIBILITY
    }

    //add params
    public static List<PGPPublicKey> getPublicKeys(){
        return null;
    }

    public static void encrypt(String inputFile, PGPEncryptor.SymetricKeyAlgorithm algorythm, List<PGPOptions> options){
        try{
            OutputStream output=new FileOutputStream(new File(inputFile+".pgp"));
            List<PGPPublicKey> publicKeys=getPublicKeys();

            if(options.contains(PGPOptions.COMPATIBILITY)){
                //PGPEncryptor.configureEncryption(algorythm,)
            }
            if(options.contains(PGPOptions.ENCRYPTION)){
                output=PGPEncryptor.configureEncryption(algorythm,publicKeys,output);
            }
            if(options.contains(PGPOptions.COMPRESSION)){
                //PGPEncryptor.configureEncryption(algorythm,)
            }
            if(options.contains(PGPOptions.AUTENTICATION)){
                //PGPEncryptor.configureEncryption(algorythm,)
            }

        }
        catch(Exception e){

        }

    }
}
