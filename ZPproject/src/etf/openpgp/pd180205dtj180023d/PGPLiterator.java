package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SignatureSubpacketTags;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.util.io.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class PGPLiterator {

    public static OutputStream configureAuthentication(OutputStream stream, String inputFile) throws IOException, PGPException {
        PGPLiteralDataGenerator generator = new PGPLiteralDataGenerator();
        return generator.open(stream, PGPLiteralData.BINARY, inputFile, new Date(), new byte [PGPProtocol.BUFFER_SIZE] );
    }

    public static void copyData(OutputStream stream, ByteArrayOutputStream content,PGPLiteralData data) throws IOException {
        Streams.pipeAll(data.getInputStream(), content);
        stream.write(content.toByteArray());
    }
}
