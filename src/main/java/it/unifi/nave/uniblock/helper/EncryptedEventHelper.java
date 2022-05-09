package it.unifi.nave.uniblock.helper;

import it.unifi.nave.uniblock.data.event.Encryptable;
import it.unifi.nave.uniblock.data.event.EncryptedEvent;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EncryptedEventHelper {
  public static EncryptedEvent build(Encryptable event, String author, List<String> receivers) {
    var payloadKey = AESHelper.randomKey();
    var payload = AESHelper.encrypt(payloadKey, HashHelper.serialize(event), false);
    var sign =
        PKHelper.sign(
            payload.getBytes(StandardCharsets.UTF_8),
            PersistenceHelper.getKeyManager().retrieveSignPk(author));
    var eventContainer = new EncryptedEvent(author, event.getType(), payload, sign);
    Stream.concat(receivers.stream(), Stream.of(author))
        .map(id -> encryptKey(id, payloadKey, author))
        .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
    return eventContainer;
  }

  private static Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
    PublicKey pbk = PersistenceHelper.getBlockchain().searchCertificate(id).dhPbk();
    PrivateKey dhPk = PersistenceHelper.getKeyManager().retrieveDhPk(author);
    return Map.entry(id, PKHelper.encrypt(dhPk, pbk, key));
  }
}
