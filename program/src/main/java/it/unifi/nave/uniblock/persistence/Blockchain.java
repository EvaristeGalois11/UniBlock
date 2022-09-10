package it.unifi.nave.uniblock.persistence;

import com.google.common.collect.Streams;
import com.google.common.primitives.Bytes;
import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.service.crypto.PKService;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Iterator;

public abstract class Blockchain implements Iterable<Block> {

  private final PKService pkService;

  public Blockchain(PKService pkService) {
    this.pkService = pkService;
  }

  public abstract void saveBlock(Block block);

  public abstract Block retrieveBlock(String hash);

  public abstract Block retrieveGenesisBlock();

  public abstract Block retrieveLastBlock();

  public Certificate searchCertificate(String userId) {
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
    return pkService.verify(
        Bytes.concat(signPbk.getEncoded(), dhPbk.getEncoded()),
        sign,
        searchGenesisCertificate().signPbk());
  }

  public Certificate searchGenesisCertificate() {
    return searchCertificate(Certificate.GENESIS);
  }

  @Override
  public Iterator<Block> iterator() {
    return new Iterator<>() {
      private final Blockchain blockchainPersistence = Blockchain.this;
      private Block current = blockchainPersistence.retrieveLastBlock();

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
    };
  }
}
