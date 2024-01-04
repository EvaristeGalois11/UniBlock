/*
 *Copyright (C) 2023 Claudio Nave
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

import com.google.common.primitives.Bytes;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AESService {
  private static final int AES_KEY_SIZE_BYTE = 16;
  private static final int GCM_TAG_SIZE_BIT = 128;
  private static final int IV_GCM_SIZE_BYTE = 12;
  private static final String SYMMETRIC_CIPHER = "AES";
  private static final String AES_SUITE = "AES/GCM/NoPadding";

  private final HashService hashService;
  private final RandomService randomService;

  @Inject
  public AESService(HashService hashService, RandomService randomService) {
    this.hashService = hashService;
    this.randomService = randomService;
  }

  public String encrypt(byte[] secret, byte[] plain, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var iv = randomService.generateRandom(IV_GCM_SIZE_BYTE);
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var cipherText = cipher.doFinal(plain);
      var result = Bytes.concat(iv, cipherText);
      return Base64.getEncoder().encodeToString(result);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  public String decrypt(byte[] secret, byte[] encrypted, boolean derive) {
    try {
      var key = derive ? deriveKey(secret) : secret;
      var cipher = Cipher.getInstance(AES_SUITE);
      var parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, encrypted, 0, IV_GCM_SIZE_BYTE);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec);
      var result = cipher.doFinal(encrypted, IV_GCM_SIZE_BYTE, encrypted.length - IV_GCM_SIZE_BYTE);
      return new String(result, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  private byte[] deriveKey(byte[] source) {
    return Arrays.copyOf(hashService.hashRaw(source), AES_KEY_SIZE_BYTE);
  }

  public byte[] randomKey() {
    return randomService.generateRandom(AES_KEY_SIZE_BYTE);
  }
}
