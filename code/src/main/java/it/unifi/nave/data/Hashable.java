package it.unifi.nave.data;

import it.unifi.nave.crypto.CryptoFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Hashable implements Serializable {
  public String hash() {
    try {
      return CryptoFactory.newHashUtil().hash(serialize());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Something went wrong", e);
    }
  }

  private byte[] serialize() throws IOException {
    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
    objectOutputStream.writeObject(this);
    return arrayOutputStream.toByteArray();
  }
}
