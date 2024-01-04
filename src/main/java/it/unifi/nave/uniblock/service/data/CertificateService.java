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
package it.unifi.nave.uniblock.service.data;

import static it.unifi.nave.uniblock.data.event.Certificate.GENESIS;

import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.service.crypto.HashService;
import it.unifi.nave.uniblock.service.crypto.PKService;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CertificateService {

  private final HashService hashService;
  private final PKService pkService;
  private final KeyManager keyManager;

  @Inject
  public CertificateService(HashService hashService, PKService pkService, KeyManager keyManager) {
    this.hashService = hashService;
    this.pkService = pkService;
    this.keyManager = keyManager;
  }

  public Certificate build(
      String name,
      PublicKey signPbk,
      PublicKey dhPbk,
      Certificate.CertificateType certificateType) {
    if (certificateType == Certificate.CertificateType.GENESIS) {
      return new Certificate(
          GENESIS, GENESIS, Certificate.CertificateType.GENESIS, signPbk, dhPbk, GENESIS);
    } else {
      return new Certificate(
          calculateUserId(signPbk, dhPbk),
          name,
          certificateType,
          signPbk,
          dhPbk,
          authorizedKey(signPbk, dhPbk));
    }
  }

  private String calculateUserId(PublicKey signPbk, PublicKey dhPbk) {
    return hashService.hash(concatPbk(signPbk, dhPbk));
  }

  private byte[] concatPbk(PublicKey signPbk, PublicKey dhPbk) {
    var signPbkEncoded = signPbk.getEncoded();
    var dhPbkEncoded = dhPbk.getEncoded();
    return ByteBuffer.allocate(signPbkEncoded.length + dhPbkEncoded.length)
        .put(signPbkEncoded)
        .put(dhPbkEncoded)
        .array();
  }

  private String authorizedKey(PublicKey signPbk, PublicKey dhPbk) {
    return pkService.sign(concatPbk(signPbk, dhPbk), keyManager.retrieveSignPk(GENESIS));
  }
}
