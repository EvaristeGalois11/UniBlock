package it.unifi.nave.uniblock.data.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncryptedEvent implements Event {
  private final String author;
  private final Encryptable.EventType eventType;
  private final String payload;
  private final String sign;
  private final Map<String, String> mapOfKeys;

  public EncryptedEvent(
      String author, Encryptable.EventType eventType, String payload, String sign) {
    this.author = author;
    this.eventType = eventType;
    this.payload = payload;
    this.sign = sign;
    mapOfKeys = new HashMap<>();
  }

  public void addKey(String id, String key) {
    mapOfKeys.put(id, key);
  }

  public String getAuthor() {
    return author;
  }

  public Encryptable.EventType getEventType() {
    return eventType;
  }

  public String getPayload() {
    return payload;
  }

  public String getSign() {
    return sign;
  }

  public Map<String, String> getMapOfKeys() {
    return Collections.unmodifiableMap(mapOfKeys);
  }
}
