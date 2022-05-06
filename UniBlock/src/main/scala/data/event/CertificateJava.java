package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.StringHelper;
import it.unifi.nave.uniblock.helper.StringHelperJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;
import it.unifi.nave.uniblock.helper.crypto.PKHelperJava;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Base64;

public record CertificateJava(String userId, String name, CertificateType certificateType,
                              PublicKey signPbk, PublicKey dhPbk, String sign) implements EventJava {
    public static final String GENESIS = "GENESIS";

    public static CertificateJava build(String name, PublicKey signPbk, PublicKey dhPbk, CertificateType certificateType) {
        if (certificateType == CertificateType.GENESIS) {
            return new CertificateJava(GENESIS, GENESIS, CertificateType.GENESIS, signPbk, dhPbk, GENESIS);
        } else {
            return new CertificateJava(calculateUserId(signPbk, dhPbk), name, certificateType, signPbk, dhPbk, authorizedKey(signPbk, dhPbk));
        }
    }

    private static String calculateUserId(PublicKey signPbk, PublicKey dhPbk) {
        return HashHelperJava.hash(concatPbk(signPbk, dhPbk));
    }

    private static byte[] concatPbk(PublicKey signPbk, PublicKey dhPbk) {
        byte[] signPbkEncoded = signPbk.getEncoded();
        byte[] dhPbkEncoded = dhPbk.getEncoded();
        return ByteBuffer.allocate(signPbkEncoded.length + dhPbkEncoded.length).put(signPbkEncoded).put(dhPbkEncoded).array();
    }

    private static String authorizedKey(PublicKey signPbk, PublicKey dhPbk) {
        // TODO Porting persistence manager
        return PKHelperJava.sign(concatPbk(signPbk, dhPbk), null/*PersistenceManager.keyManager.retrieveSignPk(GENESIS).get*/);
    }

    @Override
    public String toString() {
        return StringHelper.formatLeft(HashHelperJava.hash(this), "hash")
                + StringHelper.formatLeft(userId, "userId")
                + StringHelper.formatLeft(name, "name")
                + StringHelper.formatLeft(certificateType, "certificateType")
                + keyToString(signPbk, "sign")
                + keyToString(dhPbk, "dh")
                + StringHelper.formatLeft(sign, "signature");
    }

    private String keyToString(PublicKey pbk, String label) {
        return StringHelperJava.formatLeft(Base64.getEncoder().encodeToString(pbk.getEncoded()), label + " public key");
    }

    public enum CertificateType {
        PROFESSOR, STUDENT, GENESIS;
    }
}

