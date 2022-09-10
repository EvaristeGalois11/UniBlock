package it.unifi.nave.uniblock.service.data;

import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.service.crypto.HashService;
import it.unifi.nave.uniblock.service.crypto.PKService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.security.PublicKey;

import static it.unifi.nave.uniblock.data.event.Certificate.GENESIS;

@Singleton
public class CertificateService {

  private final HashService hashService;
  private final PKService pkService;
  private final KeyManager keyManager;

  @Inject
  public CertificateService(HashService hashService, PKService pkService, KeyManager keyManager) {
    this.hashService = hashService;
    this.pkService = pkService;
    this.keyManager = keyManager;
  }

  public Certificate build(
      String name,
      PublicKey signPbk,
      PublicKey dhPbk,
      Certificate.CertificateType certificateType) {
    if (certificateType == Certificate.CertificateType.GENESIS) {
      return new Certificate(
          GENESIS, GENESIS, Certificate.CertificateType.GENESIS, signPbk, dhPbk, GENESIS);
    } else {
      return new Certificate(
          calculateUserId(signPbk, dhPbk),
          name,
          certificateType,
          signPbk,
          dhPbk,
          authorizedKey(signPbk, dhPbk));
    }
  }

  private String calculateUserId(PublicKey signPbk, PublicKey dhPbk) {
    return hashService.hash(concatPbk(signPbk, dhPbk));
  }

  private byte[] concatPbk(PublicKey signPbk, PublicKey dhPbk) {
    byte[] signPbkEncoded = signPbk.getEncoded();
    byte[] dhPbkEncoded = dhPbk.getEncoded();
    return ByteBuffer.allocate(signPbkEncoded.length + dhPbkEncoded.length)
        .put(signPbkEncoded)
        .put(dhPbkEncoded)
        .array();
  }

  private String authorizedKey(PublicKey signPbk, PublicKey dhPbk) {
    return pkService.sign(concatPbk(signPbk, dhPbk), keyManager.retrieveSignPk(GENESIS));
  }
}
