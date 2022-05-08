package it.unifi.nave.uniblock.data.event;

import it.unifi.nave.uniblock.helper.StringHelper;
import it.unifi.nave.uniblock.helper.AESHelper;
import it.unifi.nave.uniblock.helper.HashHelper;
import it.unifi.nave.uniblock.helper.PKHelper;
import it.unifi.nave.uniblock.persistence.PersistenceManager;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EncryptedEvent implements Event {
    private String author;
    private Encryptable.EventType eventType;
    private String payload;
    private String sign;
    private Map<String, String> mapOfKeys;

    public EncryptedEvent(String author, Encryptable.EventType eventType, String payload, String sign) {
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

    public Encryptable.EventType getEventType() {
        return eventType;
    }

    public void setEventType(Encryptable.EventType eventType) {
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
        return StringHelper.formatLeft(HashHelper.hash(this), "hash") + "\n"
                + StringHelper.formatLeft(author, "author") + "\n"
                + StringHelper.formatLeft(eventType, "eventType") + "\n"
                + mapOfKeysToString() + "\n"
                + StringHelper.formatLeft(payload, "encrypted event") + "\n"
                + StringHelper.formatLeft(sign, "signature");
    }

    private String mapOfKeysToString() {
        return mapOfKeys.entrySet().stream()
                .map(e -> StringHelper.formatLeft(e.getKey(), "receiver") + "\n" + StringHelper.formatLeft(e.getValue(), "key"))
                .collect(Collectors.joining("\n"));
    }

    public static EncryptedEvent build(Encryptable event, String author, List<String> receivers) {
        var payloadKey = AESHelper.randomKey();
        var payload = AESHelper.encrypt(payloadKey, HashHelper.serialize(event), false);
        var sign = PKHelper.sign(payload.getBytes(StandardCharsets.UTF_8), PersistenceManager.getKeyManager().retrieveSignPk(author));
        var eventContainer = new EncryptedEvent(author, event.getType(), payload, sign);
        Stream.concat(receivers.stream(), Stream.of(author))
                .map(id -> encryptKey(id, payloadKey, author))
                .forEach(e -> eventContainer.addKey(e.getKey(), e.getValue()));
        return eventContainer;
    }

    private static Map.Entry<String, String> encryptKey(String id, byte[] key, String author) {
        PublicKey pbk = PersistenceManager.getBlockchain().searchCertificate(id).dhPbk();
        PrivateKey dhPk = PersistenceManager.getKeyManager().retrieveDhPk(author);
        return Map.entry(id, PKHelper.encrypt(dhPk, pbk, key));
    }

}
