package it.unifi.nave.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
  private static final int AES_KEY_SIZE_BYTE = 16;
  private static final int GCM_TAG_SIZE_BIT = 128;
  private static final int IV_GCM_SIZE_BYTE = 12;
  private static final String SYMMETRIC_CIPHER = "AES";
  private static final String AES_SUITE = "AES/GCM/NoPadding";

  public String encryptEncoded(byte[] secret, String string, boolean derive) {
    return encryptEncoded(secret, string.getBytes(StandardCharsets.UTF_8), derive);
  }

  public String encryptEncoded(byte[] secret, byte[] bytes, boolean derive) {
    return Base64.getEncoder().encodeToString(encrypt(secret, bytes, derive));
  }

  public byte[] encrypt(byte[] secret, String string, boolean derive) {
    return encrypt(secret, string.getBytes(StandardCharsets.UTF_8), derive);
  }

  public byte[] encrypt(byte[] secret, byte[] bytes, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var iv = CryptoFactory.generateRandom(IV_GCM_SIZE_BYTE);
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var cipherText = cipher.doFinal(bytes);
      var cipherMessage =
          ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
      CryptoFactory.erase(secret, key, iv);
      return cipherMessage;
    } catch (NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't encrypt the message", e);
    }
  }

  public String decryptEncoded(byte[] secret, String string, boolean derive) {
    return decryptEncoded(secret, Base64.getDecoder().decode(string), derive);
  }

  public String decryptEncoded(byte[] secret, byte[] bytes, boolean derive) {
    return new String(decrypt(secret, bytes, derive), StandardCharsets.UTF_8);
  }

  public byte[] decrypt(byte[] secret, String string, boolean derive) {
    return decrypt(secret, Base64.getDecoder().decode(string), derive);
  }

  public byte[] decrypt(byte[] secret, byte[] bytes, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, bytes, 0, IV_GCM_SIZE_BYTE);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var plainText = cipher.doFinal(bytes, IV_GCM_SIZE_BYTE, bytes.length - IV_GCM_SIZE_BYTE);
      CryptoFactory.erase(secret, key);
      return plainText;
    } catch (NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't decrypt the message", e);
    }
  }

  private byte[] deriveKey(byte[] source) {
    return Arrays.copyOf(CryptoFactory.newHashUtil().hashRaw(source), AES_KEY_SIZE_BYTE);
  }

}
