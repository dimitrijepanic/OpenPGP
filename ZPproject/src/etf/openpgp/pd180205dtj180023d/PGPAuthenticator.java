package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SignatureSubpacket;
import org.bouncycastle.bcpg.SignatureSubpacketTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSigner;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.*;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

public class PGPAuthenticator {

    private static PGPSignatureGenerator generator;
    public static void configureAuthentication(PGPSecretKey key, String password, OutputStream stream) throws IOException, PGPException {

        System.out.println(key.getPublicKey().getAlgorithm()+" "+key.getKeyEncryptionAlgorithm());
        PGPContentSignerBuilder builder=new BcPGPContentSignerBuilder(key.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);

        //identitet onog koji potpisuje
        PGPSignatureSubpacketGenerator subpacket = new PGPSignatureSubpacketGenerator();
        subpacket.addSignerUserID(false,key.getPublicKey().getUserIDs().next()); //??
        //ubaciti jos neki subpaket?

        generator=new PGPSignatureGenerator(builder);
        generator.setUnhashedSubpackets(PGPSignatureSubpacketVector
                .fromSubpackets(subpacket.getSubpackets(SignatureSubpacketTags.SIGNER_USER_ID)));

        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                .build(password.toCharArray());
        generator.init(PGPSignature.BINARY_DOCUMENT,key.extractPrivateKey(decryptor));
        PGPOnePassSignature signature=generator.generateOnePassVersion(false);
        signature.encode(stream);
    }

    public static void updateSignature(byte[] buffer, int size){
        generator.update(buffer,0,size);
    }

    public static void encode(OutputStream os) throws PGPException, IOException {
        generator.generate().encode(os);
    }
}
