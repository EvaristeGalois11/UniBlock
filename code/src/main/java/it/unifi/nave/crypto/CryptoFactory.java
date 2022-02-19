package it.unifi.nave.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

public class CryptoFactory {
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static HashUtil newHashUtil() {
    return new HashUtil();
  }

  public static PKUtil newPKUtil() {
    return new PKUtil();
  }

  public static AESUtil newAESUtil() {
    return new AESUtil();
  }

  public static byte[] generateRandom(int length) {
    var random = new byte[length];
    SECURE_RANDOM.nextBytes(random);
    return random;
  }

  public static void erase(byte[]... arrays) {
    for (var array : arrays) {
      Arrays.fill(array, (byte) 0);
    }
  }
}
