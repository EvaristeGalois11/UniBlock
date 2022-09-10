package it.unifi.nave.uniblock.helper;

import it.unifi.nave.uniblock.data.event.Encryptable;
import it.unifi.nave.uniblock.data.event.EncryptedEvent;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Singleton
public class EncryptedEventHelper {

  private final AESHelper aesHelper;
  private final HashHelper hashHelper;
  private final PKHelper pkHelper;
  private final Blockchain blockchain;
  private final KeyManager keyManager;

  @Inject
  public EncryptedEventHelper(AESHelper aesHelper, HashHelper hashHelper, PKHelper pkHelper, Blockchain blockchain, KeyManager keyManager) {
    this.aesHelper = aesHelper;
    this.hashHelper = hashHelper;
    this.pkHelper = pkHelper;
    this.blockchain = blockchain;
    this.keyManager = keyManager;
  }

  public EncryptedEvent build(Encryptable event, String author, List<String> receivers) {
    var payloadKey = aesHelper.randomKey();
    var payload = aesHelper.encrypt(payloadKey, hashHelper.serialize(event), false);
    var sign =
        pkHelper.sign(
            payload.getBytes(StandardCharsets.UTF_8),
            keyManager.retrieveSignPk(author));
    var eventContainer = new EncryptedEvent(author, event.getType(), payload, sign);
    Stream.concat(receivers.stream(), Stream.of(author))
        .map(id -> encryptKey(id, payloadKey, author))
        .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
    return eventContainer;
  }

  private Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
    PublicKey pbk = blockchain.searchCertificate(id).dhPbk();
    PrivateKey dhPk = keyManager.retrieveDhPk(author);
    return Map.entry(id, pkHelper.encrypt(dhPk, pbk, key));
  }
}
