package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.StringHelper;
import it.unifi.nave.uniblock.helper.crypto.AESHelperJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;
import it.unifi.nave.uniblock.helper.crypto.PKHelperJava;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EncryptedEventJava implements EventJava {
    private String author;
    private EncryptableJava.EventType eventType;
    private String payload;
    private String sign;
    private Map<String, String> mapOfKeys;

    public EncryptedEventJava(String author, EncryptableJava.EventType eventType, String payload, String sign) {
        this.author = author;
        this.eventType = eventType;
        this.payload = payload;
        this.sign = sign;
    }

    public void addKey(String id, String key) {
        mapOfKeys.put(id, key);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public EncryptableJava.EventType getEventType() {
        return eventType;
    }

    public void setEventType(EncryptableJava.EventType eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Map<String, String> getMapOfKeys() {
        return mapOfKeys;
    }

    public void setMapOfKeys(Map<String, String> mapOfKeys) {
        this.mapOfKeys = mapOfKeys;
    }

    public String toString() {
        return StringHelper.formatLeft(HashHelperJava.hash(this), "hash")
                + StringHelper.formatLeft(author, "author")
                + StringHelper.formatLeft(eventType, "eventType")
                + mapOfKeysToString()
                + StringHelper.formatLeft(payload, "encrypted event")
                + StringHelper.formatLeft(sign, "signature");
    }

    private String mapOfKeysToString() {
        return mapOfKeys.entrySet().stream()
                .map(e -> StringHelper.formatLeft(e.getKey(), "receiver") + "\n" + StringHelper.formatLeft(e.getValue(), "key"))
                .collect(Collectors.joining("\n"));
    }

    // TODO porting persistence manager
    public static EncryptedEventJava build(EncryptableJava event, String author, List<String> receivers) {
        var payloadKey = AESHelperJava.randomKey();
        var payload = AESHelperJava.encrypt(payloadKey, HashHelperJava.serialize(event), false);
        var sign = PKHelperJava.sign(payload.getBytes(StandardCharsets.UTF_8), null /*PersistenceManager.keyManager.retrieveSignPk(author).get*/);
        var eventContainer = new EncryptedEventJava(author, event.getType(), payload, sign);
        Stream.concat(receivers.stream(), Stream.of(author))
                .map(id -> encryptKey(id, payloadKey, author))
                .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
        return eventContainer;
    }

    // TODO porting persistence manager
    private static Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
        PublicKey pbk = null/*PersistenceManager.searchCertificate(id).dhPbk*/;
        PrivateKey dhPk = null/*PersistenceManager.keyManager.retrieveDhPk(author).get*/;
        return Map.entry(id, PKHelperJava.encrypt(dhPk, pbk, key));
    }

}
