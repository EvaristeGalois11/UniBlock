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
package it.unifi.nave.uniblock.service.crypto;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import javax.inject.Inject;
import javax.inject.Singleton;

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
