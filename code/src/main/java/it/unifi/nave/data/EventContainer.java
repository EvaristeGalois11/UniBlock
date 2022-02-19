package it.unifi.nave.data;

import java.util.HashMap;
import java.util.Map;

public class EventContainer implements Hashable {
  private String author;
  private Map<String, byte[]> mapOfKeys;
  private byte[] payload;

  public EventContainer(String author) {
    this.author = author;
    this.mapOfKeys = new HashMap<>();
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Map<String, byte[]> getMapOfKeys() {
    return mapOfKeys;
  }

  public void addKey(String id, byte[] key) {
    mapOfKeys.put(id, key);
  }

  public byte[] getPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
    this.payload = payload;
  }
}
