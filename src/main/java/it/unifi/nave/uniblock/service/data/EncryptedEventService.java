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
package it.unifi.nave.uniblock.service.data;

import it.unifi.nave.uniblock.data.event.Encryptable;
import it.unifi.nave.uniblock.data.event.EncryptedEvent;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.service.crypto.AESService;
import it.unifi.nave.uniblock.service.crypto.HashService;
import it.unifi.nave.uniblock.service.crypto.PKService;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EncryptedEventService {

  private final AESService aesService;
  private final HashService hashService;
  private final PKService pkService;
  private final Blockchain blockchain;
  private final KeyManager keyManager;

  @Inject
  public EncryptedEventService(
      AESService aesService,
      HashService hashService,
      PKService pkService,
      Blockchain blockchain,
      KeyManager keyManager) {
    this.aesService = aesService;
    this.hashService = hashService;
    this.pkService = pkService;
    this.blockchain = blockchain;
    this.keyManager = keyManager;
  }

  public EncryptedEvent build(Encryptable event, String author, List<String> receivers) {
    var payloadKey = aesService.randomKey();
    var payload = aesService.encrypt(payloadKey, hashService.serialize(event), false);
    var sign =
        pkService.sign(payload.getBytes(StandardCharsets.UTF_8), keyManager.retrieveSignPk(author));
    var eventContainer = new EncryptedEvent(author, event.getType(), payload, sign);
    Stream.concat(receivers.stream(), Stream.of(author))
        .map(id -> encryptKey(id, payloadKey, author))
        .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
    return eventContainer;
  }

  private Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
    PublicKey pbk = blockchain.searchCertificate(id).dhPbk();
    PrivateKey dhPk = keyManager.retrieveDhPk(author);
    return Map.entry(id, pkService.encrypt(dhPk, pbk, key));
  }
}
