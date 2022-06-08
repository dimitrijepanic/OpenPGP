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

    public static OutputStream configureEncryption(SymetricKeyAlgorithm algorythm, List<PGPPublicKey> publicKeys, OutputStream stream) throws IOException, PGPException {
        BcPGPDataEncryptorBuilder builder=new BcPGPDataEncryptorBuilder(algorythm.map());
        builder.setSecureRandom(new SecureRandom());
        builder.setWithIntegrityPacket(true);

        PGPEncryptedDataGenerator generator=new PGPEncryptedDataGenerator(builder);
        publicKeys.forEach(publicKey -> {
            generator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));
        });
        return generator.open(stream,new byte[PGPProtocol.BUFFER_SIZE]);
    }

    public static class DecriptionOutput{
        InputStream plainText;
        String mssg;
    }

    public static DecriptionOutput executeDecryption(PGPEncryptedDataList header, List<PGPSecretKey> secrets, String password) throws IOException, PGPException {
        DecriptionOutput output=new DecriptionOutput();
        Iterator<PGPEncryptedData> encryptedData = header.getEncryptedDataObjects();
        //mora while zbog vise public keyeva
        PGPPrivateKey old=null;
        while (encryptedData.hasNext()){
            PGPPublicKeyEncryptedData data=(PGPPublicKeyEncryptedData)encryptedData.next();
            PGPSecretKey secretKey=secrets.stream().filter(key->key.getKeyID()==data.getKeyID()).findFirst().orElse(null);
            if(secretKey==null){
                output.mssg="no secret keys with id: "+data.getKeyID();
                return output;
            }
            PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                    .build(password.toCharArray());
            PGPPrivateKey key=secretKey.extractPrivateKey(decryptor);
            if(key==null) {
                output.mssg="passphrase for key with id "+ data.getKeyID()+" not valid";
                return output;
            }
            output.plainText=data.getDataStream(new BcPublicKeyDataDecryptorFactory(key));
            old=key;
        }
        return output;
    }
}
