package it.unifi.nave.crypto;

import javax.crypto.KeyAgreement;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PKUtil {
  private static final String PUBLIC_KEY_TYPE = "X25519";
  private static final String PUBLIC_KEY_AGREEMENT = "XDH";

  public KeyPair generateKeyPair() {
    try {
      return KeyPairGenerator.getInstance(PUBLIC_KEY_TYPE).generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't produce key of the necessary type", e);
    }
  }

  public String encrypt(PrivateKey pk, PublicKey pbk, String string) {
    var secret = generateSecret(pk, pbk);
    return CryptoFactory.newAESUtil().encrypt(secret, string, true);
  }

  public String decrypt(PrivateKey pk, PublicKey pbk, String string) {
    var secret = generateSecret(pk, pbk);
    return CryptoFactory.newAESUtil().decrypt(secret, string, true);
  }

  private byte[] generateSecret(PrivateKey privateKey, PublicKey publicKey) {
    try {
      var agreement = KeyAgreement.getInstance(PUBLIC_KEY_AGREEMENT);
      agreement.init(privateKey);
      agreement.doPhase(publicKey, true);
      return agreement.generateSecret();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't generate the secret", e);
    }
  }
}
