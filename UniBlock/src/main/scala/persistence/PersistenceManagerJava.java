package it.unifi.nave.uniblock.persistence;

import com.google.common.collect.Streams;
import com.google.common.primitives.Bytes;
import it.unifi.nave.uniblock.data.block.BlockJava;
import it.unifi.nave.uniblock.data.event.CertificateJava;
import it.unifi.nave.uniblock.helper.crypto.PKHelperJava;
import it.unifi.nave.uniblock.persistence.impl.InMemoryPersistenceJava;

import java.security.PublicKey;
import java.util.Collection;

public class PersistenceManagerJava {
    private static final InMemoryPersistenceJava IN_MEMORY_PERSISTENCE = new InMemoryPersistenceJava();

    public static BlockchainJava getBlockchain() {
        return IN_MEMORY_PERSISTENCE;
    }

    public static KeyManagerJava getKeyManager() {
        return IN_MEMORY_PERSISTENCE;
    }

    public static CertificateJava searchCertificate(String userId) {
        return Streams.stream(getBlockchain())
                .map(BlockJava::getEvents)
                .flatMap(Collection::stream)
                .filter(CertificateJava.class::isInstance)
                .map(CertificateJava.class::cast)
                .filter(c -> userId.equals(c.userId()))
                .filter(PersistenceManagerJava::verifyCertificate)
                .findAny()
                .orElseThrow();
    }

    private static boolean verifyCertificate(CertificateJava certificate) {
        if (certificate.certificateType() == CertificateJava.CertificateType.GENESIS) {
            return true;
        } else {
            return verify(certificate.signPbk(), certificate.dhPbk(), certificate.sign());
        }
    }

    private static boolean verify(PublicKey signPbk, PublicKey dhPbk, String sign) {
        return PKHelperJava.verify(Bytes.concat(signPbk.getEncoded(), dhPbk.getEncoded()), sign, searchGenesisCertificate().signPbk());
    }

    public static CertificateJava searchGenesisCertificate() {
        return searchCertificate(CertificateJava.GENESIS);
    }

}
