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
import java.io.OutputStream;
import java.security.SecureRandom;
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
}
