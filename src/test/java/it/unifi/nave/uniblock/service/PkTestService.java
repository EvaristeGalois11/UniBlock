/*
 *Copyright (C) 2022-2024 Claudio Nave
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
package it.unifi.nave.uniblock.service;

import it.unifi.nave.uniblock.service.crypto.AESService;
import it.unifi.nave.uniblock.service.crypto.PKService;
import java.nio.file.Paths;
import java.security.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PkTestService extends PKService {
  private static final String TEST_KEYSTORE = "/test.p12";
  private static final char[] PASSWORD_KEYSTORE = "test1!".toCharArray();
  private static final String SIGN_ALIAS = "sign";
  private static final String DH_ALIAS = "dh";

  @Inject
  public PkTestService(AESService aesService) {
    super(aesService);
  }

  @Override
  public KeyPair generateDhKeyPair() {
    return getKeyPair(DH_ALIAS);
  }

  @Override
  public KeyPair generateSignKeyPair() {
    return getKeyPair(SIGN_ALIAS);
  }

  private KeyPair getKeyPair(String alias) {
    try {
      var keystore = getKeystore();
      var publicKey = keystore.getCertificate(alias).getPublicKey();
      var privateKey = keystore.getKey(alias, PASSWORD_KEYSTORE);
      return new KeyPair(publicKey, (PrivateKey) privateKey);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private KeyStore getKeystore() throws Exception {
    var file = Paths.get(getClass().getResource(TEST_KEYSTORE).toURI()).toFile();
    return KeyStore.getInstance(file, PASSWORD_KEYSTORE);
  }
}
