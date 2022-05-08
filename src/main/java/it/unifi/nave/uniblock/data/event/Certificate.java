package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.HashHelper;
import it.unifi.nave.uniblock.helper.StringHelper;

import java.security.PublicKey;
import java.util.Base64;

public record Certificate(String userId, String name, CertificateType certificateType,
                          PublicKey signPbk, PublicKey dhPbk, String sign) implements Event {
    public static final String GENESIS = "GENESIS";

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

