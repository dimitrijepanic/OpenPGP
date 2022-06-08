package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.*;
import org.bouncycastle.util.io.Streams;

import javax.xml.crypto.dsig.keyinfo.PGPData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PGPEncryptor {

    public enum SymetricKeyAlgorithm{
        CAST5,TRIPLEDES;

        private int map(){
            switch (this){
                case CAST5: return PGPEncryptedData.CAST5;
                case TRIPLEDES: return PGPEncryptedData.TRIPLE_DES;
                default: return -1;
            }
        }
    }

    public static List<OutputStream> configureEncryption(SymetricKeyAlgorithm algorythm, List<PGPPublicKey> publicKeys, OutputStream stream) throws IOException, PGPException, org.bouncycastle.openpgp.PGPException {
        BcPGPDataEncryptorBuilder builder=new BcPGPDataEncryptorBuilder(algorythm.map());
        builder.setSecureRandom(new SecureRandom());
        builder.setWithIntegrityPacket(true);
        List<OutputStream> outs=new ArrayList<>();
        for(PGPPublicKey publicKey:publicKeys){
            PGPEncryptedDataGenerator generator=new PGPEncryptedDataGenerator(builder);
            generator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));
            outs.add(0,generator.open((outs.size()==0)?stream:outs.get(0),new byte[PGPProtocol.BUFFER_SIZE]));
        }
        return outs;
    }

    public static class DecriptionOutput{
        InputStream plainText;
        String mssg;
    }

    public static DecriptionOutput executeDecryption(PGPEncryptedDataList header, List<PGPSecretKey> secrets, PGPProtocol.Callback callback) {
        DecriptionOutput output=new DecriptionOutput();
        Iterator<PGPEncryptedData> encryptedData = header.getEncryptedDataObjects();
        if (encryptedData.hasNext()){
            PGPPublicKeyEncryptedData data=(PGPPublicKeyEncryptedData)encryptedData.next();
            PGPSecretKey secretKey=secrets.stream().filter(key->key.getKeyID()==data.getKeyID()).findFirst().orElse(null);
            if(secretKey==null){
                output.mssg="no secret keys with id: "+Long.toUnsignedString(data.getKeyID());
                return output;
            }
            String password=callback.call(secretKey);
            PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                    .build(password.toCharArray());

            PGPPrivateKey key= null;
            try {
                key = secretKey.extractPrivateKey(decryptor);
                if(key==null) {
                    output.mssg="passphrase for key with id "+ Long.toUnsignedString(data.getKeyID())+" not valid";
                    return output;
                }
                output.plainText=data.getDataStream(new BcPublicKeyDataDecryptorFactory(key));
            } catch (org.bouncycastle.openpgp.PGPException e) {
                output.mssg="passphrase for key with id "+ Long.toUnsignedString(data.getKeyID())+" not valid";
                return output;
            }

        }
        return output;
    }
}
