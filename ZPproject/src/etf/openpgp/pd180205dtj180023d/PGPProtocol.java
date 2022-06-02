package etf.openpgp.pd180205dtj180023d;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

import java.io.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PGPProtocol {
    public static final int BUFFER_SIZE=1<<16;

    public enum PGPOptions {
        AUTENTICATION, ENCRYPTION, COMPRESSION, COMPATIBILITY
    }

    //add params
    public static List<PGPPublicKey> getPublicKeys(List<MyKeyRing> rings) throws PGPException {
        return rings.stream().map(ring-> ring.getPublicKeyRing().getPublicKey()).collect(Collectors.toList());
    }

    public static void encrypt(String inputFile, PGPEncryptor.SymetricKeyAlgorithm algorythm, List<PGPOptions> options, List<MyKeyRing> publicKeyRings){
        try{
            OutputStream output=new FileOutputStream(new File(inputFile+".pgp"));
            InputStream input=new FileInputStream(new File(inputFile));
            List<PGPPublicKey> publicKeys=getPublicKeys(publicKeyRings);

            if(options.contains(PGPOptions.COMPATIBILITY)){
                output=new Base64OutputStream(output);
            }
            if(options.contains(PGPOptions.ENCRYPTION)){
                output=PGPEncryptor.configureEncryption(algorythm,publicKeys,output);
            }
            if(options.contains(PGPOptions.COMPRESSION)){
                PGPCompressedDataGenerator compression=new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
                output=compression.open(output);
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
