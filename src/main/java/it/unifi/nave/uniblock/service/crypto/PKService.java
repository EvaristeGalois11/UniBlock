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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import javax.crypto.KeyAgreement;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PKService {
  private static final String PUBLIC_KEY_DH = "X25519";
  private static final String PUBLIC_KEY_AGREEMENT = "XDH";
  private static final String PUBLIC_KEY_SIGN = "Ed25519";

  private final AESService aesService;

  @Inject
  public PKService(AESService aesService) {
    this.aesService = aesService;
  }

  public KeyPair generateDhKeyPair() {
    return generateKeyPair(PUBLIC_KEY_DH);
  }

  public KeyPair generateSignKeyPair() {
    return generateKeyPair(PUBLIC_KEY_SIGN);
  }

  private KeyPair generateKeyPair(String typeKp) {
    try {
      return KeyPairGenerator.getInstance(typeKp).generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public String encrypt(PrivateKey pk, PublicKey pbk, byte[] plain) {
    var secret = generateSecret(pk, pbk);
    return aesService.encrypt(secret, plain, true);
  }

  public String decrypt(PrivateKey pk, PublicKey pbk, byte[] encrypted) {
    var secret = generateSecret(pk, pbk);
    return aesService.decrypt(secret, encrypted, true);
  }

  private byte[] generateSecret(PrivateKey privateKey, PublicKey publicKey) {
    try {
      var agreement = KeyAgreement.getInstance(PUBLIC_KEY_AGREEMENT);
      agreement.init(privateKey);
      agreement.doPhase(publicKey, true);
      return agreement.generateSecret();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String sign(byte[] toSign, PrivateKey pk) {
    try {
      var signature = Signature.getInstance(PUBLIC_KEY_SIGN);
      signature.initSign(pk);
      signature.update(toSign);
      return Base64.getEncoder().encodeToString(signature.sign());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean verify(byte[] toVerify, String sign, PublicKey pbk) {
    try {
      var sig = Signature.getInstance(PUBLIC_KEY_SIGN);
      sig.initVerify(pbk);
      sig.update(toVerify);
      return sig.verify(Base64.getDecoder().decode(sign));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
