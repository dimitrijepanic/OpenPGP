package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.openpgp.PGPPublicKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Util {

    public static Object[] generateTableRow(MyKeyRing ring){
        PGPPublicKey pk = ring.getPublicKeyRing().getPublicKey();
        String userId = pk.getUserIDs().next() + "";
        String keyId = pk.getKeyID() + "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timestamp = dateFormat.format(pk.getCreationTime());
        return new Object[] {userId, keyId, timestamp};
    }
}
