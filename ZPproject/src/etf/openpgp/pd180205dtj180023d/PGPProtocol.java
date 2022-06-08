package etf.openpgp.pd180205dtj180023d;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.util.io.Streams;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PGPProtocol {
    public static final int BUFFER_SIZE=1<<16;

    public enum PGPOptions {
        AUTENTICATION, ENCRYPTION, COMPRESSION, COMPATIBILITY
    }

    public static interface Callback{
        String call(PGPSecretKey key);
    }

    //add params
    public static List<PGPPublicKey> getPublicKeys(List<MyKeyRing> rings) throws PGPException {
        return rings.stream().map(ring-> ring.getPublicKeyRing().getPublicKey()).collect(Collectors.toList());
    }

    public static List<PGPSecretKey> getSecretKeys(List<MyKeyRing> rings) throws PGPException {
        return rings.stream().map(ring-> ring.getSecretKeyRing().getSecretKey()).collect(Collectors.toList());
    }
    public static void encrypt(String inputFile, PGPEncryptor.SymetricKeyAlgorithm algorithm, List<PGPOptions> options, List<MyKeyRing> publicKeyRings, MyKeyRing secretKey, String password) throws PGPException {
        try(OutputStream output=new FileOutputStream(new File(inputFile+"_encrypted.pgp")))
        {
            InputStream input=new FileInputStream(new File(inputFile));
            List<PGPPublicKey> publicKeys=getPublicKeys(publicKeyRings);
            OutputStream comout=output;
            if(options.contains(PGPOptions.COMPATIBILITY)){
                comout=new ArmoredOutputStream(output);
            }
            OutputStream enout=comout;
            List<OutputStream> outs=null;
            if(options.contains(PGPOptions.ENCRYPTION)){
                outs=PGPEncryptor.configureEncryption(algorithm,publicKeys,comout);
                enout=outs.get(0);
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
            if(enout!=comout){
                for(OutputStream outputStream:outs){
                    outputStream.close();
                }
            }
            if(output!=comout)comout.close();
        }
        catch (PGPException e){
            throw new PGPException(e.getMessage());
        }
        catch(Exception e){
            throw new PGPException(e+"");
        }

    }


    public static void decrypt(String inputFile, List<MyKeyRing> keyRings, Callback callback) throws PGPException {
        String outputFile=inputFile.replaceAll("_encrypted.pgp","_decrypted.txt");
        try(OutputStream output=new FileOutputStream(new File(outputFile)))
        {
            InputStream input = new FileInputStream(new File(inputFile));
            //skida Armour !!!
            PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(input), new BcKeyFingerprintCalculator());
            Iterator<Object> it=factory.iterator();
            PGPOnePassSignatureList onePassHeader=null;
            ByteArrayOutputStream content=null;
            while(it.hasNext()){
                Object header=it.next();
                System.out.println(header.getClass());
                if(header instanceof PGPEncryptedDataList){
                    PGPEncryptor.DecriptionOutput decOut=PGPEncryptor.executeDecryption((PGPEncryptedDataList)header, getSecretKeys(keyRings),callback);
                    if(decOut.mssg!=null){
                        throw new Exception(decOut.mssg);
                    }
                    factory=new PGPObjectFactory(decOut.plainText, new BcKeyFingerprintCalculator());
                    it=factory.iterator();
                }
                if(header instanceof PGPCompressedData){
                    factory = new PGPObjectFactory(((PGPCompressedData)header).getDataStream(), new BcKeyFingerprintCalculator());
                    it=factory.iterator();
                }
                if(header instanceof PGPLiteralData){
                    PGPLiterator.copyData(output,(content=new ByteArrayOutputStream()),(PGPLiteralData) header);
                }
                if (header instanceof PGPOnePassSignatureList) {
                    onePassHeader = (PGPOnePassSignatureList) header;
                }

                if (header instanceof PGPSignatureList) {
                    PGPAuthenticator.ValidationOutput out=PGPAuthenticator.validate(onePassHeader,(PGPSignatureList)header, getPublicKeys(keyRings),content);
                    if(out.msg!=null){
                        throw new Exception(out.msg);
                    }
                    System.out.println(out.key.getUserIDs().next());
                }
            }
        }
        catch(Exception e){
            throw new PGPException(e.getMessage());
        }
    }

}
