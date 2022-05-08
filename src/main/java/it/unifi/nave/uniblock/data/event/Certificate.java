package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.StringHelper;
import it.unifi.nave.uniblock.helper.HashHelper;
import it.unifi.nave.uniblock.helper.PKHelper;
import it.unifi.nave.uniblock.persistence.PersistenceManager;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Base64;

public record Certificate(String userId, String name, CertificateType certificateType,
                          PublicKey signPbk, PublicKey dhPbk, String sign) implements Event {
    public static final String GENESIS = "GENESIS";

    public static Certificate build(String name, PublicKey signPbk, PublicKey dhPbk, CertificateType certificateType) {
        if (certificateType == CertificateType.GENESIS) {
            return new Certificate(GENESIS, GENESIS, CertificateType.GENESIS, signPbk, dhPbk, GENESIS);
        } else {
            return new Certificate(calculateUserId(signPbk, dhPbk), name, certificateType, signPbk, dhPbk, authorizedKey(signPbk, dhPbk));
        }
    }

    private static String calculateUserId(PublicKey signPbk, PublicKey dhPbk) {
        return HashHelper.hash(concatPbk(signPbk, dhPbk));
    }

    private static byte[] concatPbk(PublicKey signPbk, PublicKey dhPbk) {
        byte[] signPbkEncoded = signPbk.getEncoded();
        byte[] dhPbkEncoded = dhPbk.getEncoded();
        return ByteBuffer.allocate(signPbkEncoded.length + dhPbkEncoded.length).put(signPbkEncoded).put(dhPbkEncoded).array();
    }

    private static String authorizedKey(PublicKey signPbk, PublicKey dhPbk) {
        return PKHelper.sign(concatPbk(signPbk, dhPbk), PersistenceManager.getKeyManager().retrieveSignPk(GENESIS));
    }

    @Override
    public String toString() {
        return StringHelper.formatLeft(HashHelper.hash(this), "hash") + "\n"
                + StringHelper.formatLeft(userId, "userId") + "\n"
                + StringHelper.formatLeft(name, "name") + "\n"
                + StringHelper.formatLeft(certificateType, "certificateType") + "\n"
                + keyToString(signPbk, "sign") + "\n"
                + keyToString(dhPbk, "dh") + "\n"
                + StringHelper.formatLeft(sign, "signature");
    }

    private String keyToString(PublicKey pbk, String label) {
        return StringHelper.formatLeft(Base64.getEncoder().encodeToString(pbk.getEncoded()), label + " public key");
    }

    public enum CertificateType {
        PROFESSOR, STUDENT, GENESIS;
    }
}

