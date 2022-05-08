package it.unifi.nave.uniblock.persistence;

import com.google.common.collect.Streams;
import com.google.common.primitives.Bytes;
import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.helper.PKHelper;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public interface Blockchain extends Iterable<Block> {
    void saveBlock(Block block);

    Block retrieveBlock(String hash);

    Block retrieveGenesisBlock();

    Block retrieveLastBlock();

    default Certificate searchCertificate(String userId) {
        return Streams.stream(this)
                .map(Block::getEvents)
                .flatMap(Collection::stream)
                .filter(Certificate.class::isInstance)
                .map(Certificate.class::cast)
                .filter(c -> userId.equals(c.userId()))
                .filter(this::verifyCertificate)
                .findAny()
                .orElseThrow();
    }

    private boolean verifyCertificate(Certificate certificate) {
        if (certificate.certificateType() == Certificate.CertificateType.GENESIS) {
            return true;
        } else {
            return verify(certificate.signPbk(), certificate.dhPbk(), certificate.sign());
        }
    }

    private boolean verify(PublicKey signPbk, PublicKey dhPbk, String sign) {
        return PKHelper.verify(Bytes.concat(signPbk.getEncoded(), dhPbk.getEncoded()), sign, searchGenesisCertificate().signPbk());
    }

    default Certificate searchGenesisCertificate() {
        return searchCertificate(Certificate.GENESIS);
    }

    @Override
    default Iterator<Block> iterator() {
        return new BlockchainIteratorJava(this);
    }

    class BlockchainIteratorJava implements Iterator<Block> {
        private final Blockchain blockchainPersistence;
        private Block current;

        public BlockchainIteratorJava(Blockchain blockchainPersistence) {
            this.blockchainPersistence = blockchainPersistence;
            current = blockchainPersistence.retrieveLastBlock();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Block next() {
            var buffer = current;
            current = blockchainPersistence.retrieveBlock(current.getBlockHeader().getPreviousHash());
            return buffer;
        }
    }
}
