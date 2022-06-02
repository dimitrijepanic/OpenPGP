package etf.openpgp.pd180205dtj180023d;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;
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

    public static void encrypt(String inputFile, PGPEncryptor.SymetricKeyAlgorithm algorythm, List<PGPOptions> options, List<MyKeyRing> publicKeyRings, MyKeyRing secretKey, String password){
        try(OutputStream output=new FileOutputStream(new File(inputFile+".pgp")))
        {
            InputStream input=new FileInputStream(new File(inputFile));
            List<PGPPublicKey> publicKeys=getPublicKeys(publicKeyRings);
            OutputStream comout=output;
            if(options.contains(PGPOptions.COMPATIBILITY)){
                comout=new ArmoredOutputStream(output);
            }
            OutputStream enout=comout;
            if(options.contains(PGPOptions.ENCRYPTION)){
                enout=PGPEncryptor.configureEncryption(algorythm,publicKeys,comout);
            }
            OutputStream zipout=enout;
            if(options.contains(PGPOptions.COMPRESSION)){
                PGPCompressedDataGenerator compression=new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
                zipout=compression.open(enout);
            }
            if(options.contains(PGPOptions.AUTENTICATION)){
                PGPAuthenticator.configureAuthentication(secretKey.getSecretKeyRing().getSecretKey(),password,zipout);
            }
            OutputStream litout = PGPLiterator.configureAuthentication(zipout,inputFile);

            byte[] buffer=new byte[BUFFER_SIZE];
            int size=-1;
            while ((size=input.read(buffer))!=-1){
                litout.write(buffer,0,size);
                if(options.contains(PGPOptions.AUTENTICATION)) PGPAuthenticator.updateSignature(buffer,size);
            }
            litout.close();
            if(options.contains(PGPOptions.AUTENTICATION)) PGPAuthenticator.encode(litout);
            if(zipout!=enout)zipout.close();
            if(enout!=comout)enout.close();
            if(output!=enout)comout.close();
        }
        catch(Exception e){
            System.err.println(e);
        }

    }

}
