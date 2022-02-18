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

  public String encrypt(byte[] secret, String string, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var iv = CryptoFactory.generateRandom(IV_GCM_SIZE_BYTE);
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var cipherText = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
      var cipherMessage =
          ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
      erase(secret, key, iv);
      return Base64.getEncoder().encodeToString(cipherMessage);
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

  public String decrypt(byte[] secret, String string, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var cipherMessage = Base64.getDecoder().decode(string);
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec =
          new GCMParameterSpec(GCM_TAG_SIZE_BIT, cipherMessage, 0, IV_GCM_SIZE_BYTE);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var plainText =
          cipher.doFinal(cipherMessage, IV_GCM_SIZE_BYTE, cipherMessage.length - IV_GCM_SIZE_BYTE);
      erase(secret, key);
      return new String(plainText, StandardCharsets.UTF_8);
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

  private void erase(byte[]... arrays) {
    for (var array : arrays) {
      Arrays.fill(array, (byte) 0);
    }
  }
}
