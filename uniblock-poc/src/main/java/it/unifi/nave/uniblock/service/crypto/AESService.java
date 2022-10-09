package it.unifi.nave.uniblock.service.crypto;

import com.google.common.primitives.Bytes;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Singleton
public class AESService {
  private static final int AES_KEY_SIZE_BYTE = 16;
  private static final int GCM_TAG_SIZE_BIT = 128;
  private static final int IV_GCM_SIZE_BYTE = 12;
  private static final String SYMMETRIC_CIPHER = "AES";
  private static final String AES_SUITE = "AES/GCM/NoPadding";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final HashService hashService;

  @Inject
  public AESService(HashService hashService) {
    this.hashService = hashService;
  }

  public String encrypt(byte[] secret, byte[] plain, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var iv = generateRandom(IV_GCM_SIZE_BYTE);
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var cipherText = cipher.doFinal(plain);
      var result = Bytes.concat(iv, cipherText);
      return Base64.getEncoder().encodeToString(result);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  public String decrypt(byte[] secret, byte[] encrypted, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, encrypted, 0, IV_GCM_SIZE_BYTE);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var result = cipher.doFinal(encrypted, IV_GCM_SIZE_BYTE, encrypted.length - IV_GCM_SIZE_BYTE);
      return new String(result, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  private byte[] deriveKey(byte[] source) {
    return Arrays.copyOf(hashService.hashRaw(source), AES_KEY_SIZE_BYTE);
  }

  public byte[] randomKey() {
    return generateRandom(AES_KEY_SIZE_BYTE);
  }

  private byte[] generateRandom(int length) {
    var random = new byte[length];
    SECURE_RANDOM.nextBytes(random);
    return random;
  }
}
