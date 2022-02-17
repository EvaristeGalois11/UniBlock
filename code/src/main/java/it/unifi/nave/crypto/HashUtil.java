package it.unifi.nave.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashUtil {
  private static final String HASH_TYPE = "SHA3-256";

  public String hash(String string) {
    return hash(string.getBytes(StandardCharsets.UTF_8));
  }

  public String hash(byte[] bytes) {
    return HexFormat.of().formatHex(hashRaw(bytes));
  }

  public byte[] hashRaw(String string) {
    return hashRaw(string.getBytes(StandardCharsets.UTF_8));
  }

  public byte[] hashRaw(byte[] bytes) {
    try {
      return MessageDigest.getInstance(HASH_TYPE).digest(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't produce hash of the necessary type", e);
    }
  }
}
