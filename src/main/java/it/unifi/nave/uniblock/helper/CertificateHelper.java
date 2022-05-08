package it.unifi.nave.uniblock.helper;

import it.unifi.nave.uniblock.data.event.Certificate;

import java.nio.ByteBuffer;
import java.security.PublicKey;

import static it.unifi.nave.uniblock.data.event.Certificate.GENESIS;

public class CertificateHelper {
    public static Certificate build(String name, PublicKey signPbk, PublicKey dhPbk, Certificate.CertificateType certificateType) {
        if (certificateType == Certificate.CertificateType.GENESIS) {
            return new Certificate(GENESIS, GENESIS, Certificate.CertificateType.GENESIS, signPbk, dhPbk, GENESIS);
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
        return PKHelper.sign(concatPbk(signPbk, dhPbk), PersistenceHelper.getKeyManager().retrieveSignPk(GENESIS));
    }
}
