/*
 *Copyright (C) 2022-2023 Claudio Nave
 *
 *This file is part of UniBlock.
 *
 *UniBlock is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *UniBlock is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with UniBlock. If not, see <https://www.gnu.org/licenses/>.
 */
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
