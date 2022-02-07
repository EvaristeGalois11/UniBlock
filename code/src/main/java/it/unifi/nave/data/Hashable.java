package it.unifi.nave.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Hashable implements Serializable {
  private static final String HASH_TYPE = "SHA3-256";

  public String hash() {
    try {
      ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
      objectOutputStream.writeObject(this);
      byte[] hash = MessageDigest.getInstance(HASH_TYPE).digest(arrayOutputStream.toByteArray());
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException("This jre can't produce hash of the necessary type", e);
    }
  }
}
