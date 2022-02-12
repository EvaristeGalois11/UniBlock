package it.unifi.nave.data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashUtil {
  private static final String HASH_TYPE = "SHA3-256";

  public static String hash(String string) {
    return hash(string.getBytes(StandardCharsets.UTF_8));
  }

  public static String hash(byte[] bytes) {
    try {
      byte[] hash = MessageDigest.getInstance(HASH_TYPE).digest(bytes);
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't produce hash of the necessary type", e);
    }
  }
}
