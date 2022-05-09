package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.HashHelper;
import it.unifi.nave.uniblock.helper.StringHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

  public String toString() {
    return StringHelper.formatLeft(HashHelper.hash(this), "hash")
        + "\n"
        + StringHelper.formatLeft(author, "author")
        + "\n"
        + StringHelper.formatLeft(eventType, "eventType")
        + "\n"
        + mapOfKeysToString()
        + "\n"
        + StringHelper.formatLeft(payload, "encrypted event")
        + "\n"
        + StringHelper.formatLeft(sign, "signature");
  }

  private String mapOfKeysToString() {
    return mapOfKeys.entrySet().stream()
        .map(
            e ->
                StringHelper.formatLeft(e.getKey(), "receiver")
                    + "\n"
                    + StringHelper.formatLeft(e.getValue(), "key"))
        .collect(Collectors.joining("\n"));
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
