package it.unifi.nave.uniblock.helper;

import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.persistence.KeyManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.security.PublicKey;

import static it.unifi.nave.uniblock.data.event.Certificate.GENESIS;

@Singleton
public class CertificateHelper {

  private final HashHelper hashHelper;
  private final PKHelper pkHelper;
  private final KeyManager keyManager;

  @Inject
  public CertificateHelper(HashHelper hashHelper, PKHelper pkHelper, KeyManager keyManager) {
    this.hashHelper = hashHelper;
    this.pkHelper = pkHelper;
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
    return hashHelper.hash(concatPbk(signPbk, dhPbk));
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
    return pkHelper.sign(concatPbk(signPbk, dhPbk), keyManager.retrieveSignPk(GENESIS));
  }
}
