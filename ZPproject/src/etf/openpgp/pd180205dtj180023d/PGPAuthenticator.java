package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SignatureSubpacket;
import org.bouncycastle.bcpg.SignatureSubpacketTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSigner;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.*;

import java.io.ByteArrayOutputStream;
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
        PGPOnePassSignature signature=null;
        try {
            generator.init(PGPSignature.BINARY_DOCUMENT,key.extractPrivateKey(decryptor));
            signature=generator.generateOnePassVersion(false);
            signature.encode(stream);
        } catch (org.bouncycastle.openpgp.PGPException e) {
            throw new PGPException("passphrase for key "+Long.toUnsignedString(key.getKeyID())+" not correct");
        }
    }

    public static void updateSignature(byte[] buffer, int size){
        generator.update(buffer,0,size);
    }

    public static void encode(OutputStream os) throws IOException, org.bouncycastle.openpgp.PGPException {
        generator.generate().encode(os);
    }

    public static class ValidationOutput{
        public PGPPublicKey key;
        public String msg;
    }
    public static ValidationOutput validate(PGPOnePassSignatureList header, PGPSignatureList signatures, List<PGPPublicKey> publicKeys, ByteArrayOutputStream content) throws IOException, org.bouncycastle.openpgp.PGPException {
        System.out.println(signatures.size());
        ValidationOutput output=new ValidationOutput();
        PGPOnePassSignature data = header.get(0);
        PGPPublicKey publicKey=publicKeys.stream().filter(key->key.getKeyID()==data.getKeyID()).findFirst().orElse(null);
        if(publicKey==null){
            output.msg="public key with id "+Long.toUnsignedString(data.getKeyID())+" does not exist";
            return output;
        }
        data.init(new BcPGPContentVerifierBuilderProvider(), publicKey);
        content.close();
        data.update(content.toByteArray());
        PGPSignature signature = signatures.get(0);
        try {
            if(!data.verify(signature)){
                output.msg= "signature validation failed";
                return output;
            }
        } catch (org.bouncycastle.openpgp.PGPException e) {
            output.msg= "signature validation failed";
            return output;
        }
        output.key=publicKey;
        return output;
    }
}
