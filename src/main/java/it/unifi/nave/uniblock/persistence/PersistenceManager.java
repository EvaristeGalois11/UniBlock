package it.unifi.nave.uniblock.persistence;

import com.google.common.collect.Streams;
import com.google.common.primitives.Bytes;
import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.helper.crypto.PKHelper;
import it.unifi.nave.uniblock.persistence.impl.InMemoryPersistence;

import java.security.PublicKey;
import java.util.Collection;

public class PersistenceManager {
    private static final InMemoryPersistence IN_MEMORY_PERSISTENCE = new InMemoryPersistence();

    public static Blockchain getBlockchain() {
        return IN_MEMORY_PERSISTENCE;
    }

    public static KeyManager getKeyManager() {
        return IN_MEMORY_PERSISTENCE;
    }

    public static Certificate searchCertificate(String userId) {
        return Streams.stream(getBlockchain())
                .map(Block::getEvents)
                .flatMap(Collection::stream)
                .filter(Certificate.class::isInstance)
                .map(Certificate.class::cast)
                .filter(c -> userId.equals(c.userId()))
                .filter(PersistenceManager::verifyCertificate)
                .findAny()
                .orElseThrow();
    }

    private static boolean verifyCertificate(Certificate certificate) {
        if (certificate.certificateType() == Certificate.CertificateType.GENESIS) {
            return true;
        } else {
            return verify(certificate.signPbk(), certificate.dhPbk(), certificate.sign());
        }
    }

    private static boolean verify(PublicKey signPbk, PublicKey dhPbk, String sign) {
        return PKHelper.verify(Bytes.concat(signPbk.getEncoded(), dhPbk.getEncoded()), sign, searchGenesisCertificate().signPbk());
    }

    public static Certificate searchGenesisCertificate() {
        return searchCertificate(Certificate.GENESIS);
    }

}