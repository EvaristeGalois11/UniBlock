package it.unifi.nave.data;

import it.unifi.nave.crypto.CryptoFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Hashable extends Serializable {
  default String hash() {
    return CryptoFactory.newHashUtil().hash(serialize());
  }

  default byte[] serialize() {
    try {
      ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
      objectOutputStream.writeObject(this);
      return arrayOutputStream.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Something went wrong", e);
    }
  }
}
