package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.StringHelperJava;
import it.unifi.nave.uniblock.helper.crypto.AESHelperJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;
import it.unifi.nave.uniblock.helper.crypto.PKHelperJava;
import it.unifi.nave.uniblock.persistence.PersistenceManagerJava;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
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
        mapOfKeys = new HashMap<>();
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
        return StringHelperJava.formatLeft(HashHelperJava.hash(this), "hash") + "\n"
                + StringHelperJava.formatLeft(author, "author") + "\n"
                + StringHelperJava.formatLeft(eventType, "eventType") + "\n"
                + mapOfKeysToString() + "\n"
                + StringHelperJava.formatLeft(payload, "encrypted event") + "\n"
                + StringHelperJava.formatLeft(sign, "signature");
    }

    private String mapOfKeysToString() {
        return mapOfKeys.entrySet().stream()
                .map(e -> StringHelperJava.formatLeft(e.getKey(), "receiver") + "\n" + StringHelperJava.formatLeft(e.getValue(), "key"))
                .collect(Collectors.joining("\n"));
    }

    public static EncryptedEventJava build(EncryptableJava event, String author, List<String> receivers) {
        var payloadKey = AESHelperJava.randomKey();
        var payload = AESHelperJava.encrypt(payloadKey, HashHelperJava.serialize(event), false);
        var sign = PKHelperJava.sign(payload.getBytes(StandardCharsets.UTF_8), PersistenceManagerJava.getKeyManager().retrieveSignPk(author).orElseThrow());
        var eventContainer = new EncryptedEventJava(author, event.getType(), payload, sign);
        Stream.concat(receivers.stream(), Stream.of(author))
                .map(id -> encryptKey(id, payloadKey, author))
                .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
        return eventContainer;
    }

    private static Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
        PublicKey pbk = PersistenceManagerJava.searchCertificate(id).dhPbk();
        PrivateKey dhPk = PersistenceManagerJava.getKeyManager().retrieveDhPk(author).orElseThrow();
        return Map.entry(id, PKHelperJava.encrypt(dhPk, pbk, key));
    }

}
