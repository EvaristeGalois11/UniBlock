package it.unifi.nave.uniblock.service.crypto;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Singleton
public class HashService {
  private static final String HASH_TYPE = "SHA3-256";

  @Inject
  public HashService() {}

  public String hash(String toHash) {
    return hash(toHash.getBytes(StandardCharsets.UTF_8));
  }

  public String hash(byte[] toHash) {
    return HexFormat.of().formatHex(hashRaw(toHash));
  }

  public byte[] hashRaw(byte[] toHash) {
    try {
      return MessageDigest.getInstance(HASH_TYPE).digest(toHash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public String hash(Serializable obj) {
    return hash(serialize(obj));
  }

  public byte[] serialize(Serializable obj) {
    try {
      var arrayOutputStream = new ByteArrayOutputStream();
      var objectOutputStream = new ObjectOutputStream(arrayOutputStream);
      objectOutputStream.writeObject(obj);
      return arrayOutputStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
