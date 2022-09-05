package it.unifi.nave.uniblock.helper;

import javax.crypto.KeyAgreement;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class PKHelper {
  private static final String PUBLIC_KEY_DH = "X25519";
  private static final String PUBLIC_KEY_AGREEMENT = "XDH";
  private static final String PUBLIC_KEY_SIGN = "Ed25519";

  public static KeyPair generateDhKeyPair() {
    return generateKeyPair(PUBLIC_KEY_DH);
  }

  public static KeyPair generateSignKeyPair() {
    return generateKeyPair(PUBLIC_KEY_SIGN);
  }

  private static KeyPair generateKeyPair(String typeKp) {
    try {
      return KeyPairGenerator.getInstance(typeKp).generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String encrypt(PrivateKey pk, PublicKey pbk, byte[] plain) {
    var secret = generateSecret(pk, pbk);
    return AESHelper.encrypt(secret, plain, true);
  }

  public static String decrypt(PrivateKey pk, PublicKey pbk, byte[] encrypted) {
    var secret = generateSecret(pk, pbk);
    return AESHelper.decrypt(secret, encrypted, true);
  }

  private static byte[] generateSecret(PrivateKey privateKey, PublicKey publicKey) {
    try {
      var agreement = KeyAgreement.getInstance(PUBLIC_KEY_AGREEMENT);
      agreement.init(privateKey);
      agreement.doPhase(publicKey, true);
      return agreement.generateSecret();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String sign(byte[] toSign, PrivateKey pk) {
    try {
      var signature = Signature.getInstance(PUBLIC_KEY_SIGN);
      signature.initSign(pk);
      signature.update(toSign);
      return Base64.getEncoder().encodeToString(signature.sign());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean verify(byte[] toVerify, String sign, PublicKey pbk) {
    try {
      var sig = Signature.getInstance(PUBLIC_KEY_SIGN);
      sig.initVerify(pbk);
      sig.update(toVerify);
      return sig.verify(Base64.getDecoder().decode(sign));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
