package it.unifi.nave.crypto;

import javax.crypto.KeyAgreement;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class PKUtil {
  private static final String PUBLIC_KEY_DH = "X25519";
  private static final String PUBLIC_KEY_AGREEMENT = "XDH";
  private static final String PUBLIC_KEY_SIGN = "Ed25519";
  private static final int SIGN_SIZE_BYTE = 64;

  public KeyPair generateDhKeyPair() {
    return generateKeyPair(PUBLIC_KEY_DH);
  }

  public KeyPair generateSignKeyPair() {
    return generateKeyPair(PUBLIC_KEY_SIGN);
  }

  private KeyPair generateKeyPair(String type) {
    try {
      return KeyPairGenerator.getInstance(type).generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't produce key of the necessary type", e);
    }
  }

  public String encryptEncoded(PrivateKey pk, PublicKey pbk, String string) {
    return encryptEncoded(pk, pbk, string.getBytes(StandardCharsets.UTF_8));
  }

  public String encryptEncoded(PrivateKey pk, PublicKey pbk, byte[] bytes) {
    return Base64.getEncoder().encodeToString(encrypt(pk, pbk, bytes));
  }

  public byte[] encrypt(PrivateKey pk, PublicKey pbk, String string) {
    return encrypt(pk, pbk, string.getBytes(StandardCharsets.UTF_8));
  }

  public byte[] encrypt(PrivateKey pk, PublicKey pbk, byte[] string) {
    var secret = generateSecret(pk, pbk);
    return CryptoFactory.newAESUtil().encrypt(secret, string, true);
  }

  public String decryptEncoded(PrivateKey pk, PublicKey pbk, String string) {
    var secret = generateSecret(pk, pbk);
    return CryptoFactory.newAESUtil().decryptEncoded(secret, string, true);
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

  public byte[] sign(byte[] bytes, PrivateKey pk) {
    try {
      var signature = Signature.getInstance(PUBLIC_KEY_SIGN);
      signature.initSign(pk);
      signature.update(bytes);
      var sign = signature.sign();
      return ByteBuffer.allocate(sign.length + bytes.length).put(sign).put(bytes).array();
    } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't sign the data", e);
    }
  }

  public boolean verify(byte[] bytes, PublicKey pbk) {
    try {
      Signature sig = Signature.getInstance(PUBLIC_KEY_SIGN);
      sig.initVerify(pbk);
      sig.update(bytes, SIGN_SIZE_BYTE, bytes.length - SIGN_SIZE_BYTE);
      return sig.verify(bytes, 0, SIGN_SIZE_BYTE);
    } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't sign the data", e);
    }
  }
}
