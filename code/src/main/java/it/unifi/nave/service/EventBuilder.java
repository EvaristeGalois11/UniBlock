package it.unifi.nave.service;

import it.unifi.nave.crypto.CryptoFactory;
import it.unifi.nave.data.EventContainer;
import it.unifi.nave.data.event.Event;

import java.security.KeyPair;
import java.security.PublicKey;

public class EventBuilder {
  private KeyPair dhPair;
  private KeyPair signPair;
  private EventContainer container;
  private byte[] payloadKey;

  public EventBuilder(Event event) {
    initKeyPair();
    payloadKey = CryptoFactory.generateRandom(16);
    encryptPayload(event);
  }

  private void initKeyPair() {
    throw new UnsupportedOperationException();
  }

  private void encryptPayload(Event event) {
    var idAuthor = retrieveIdUser();
    container = new EventContainer(idAuthor);
    container.setPayload(encryptAndSign(payloadKey, event));
    addKey(idAuthor, dhPair.getPublic());
  }

  private String retrieveIdUser() {
    throw new UnsupportedOperationException();
  }

  public void addKey(String id) {
    addKey(id, retrievePbk(id));
  }

  public void addKey(String id, PublicKey pbk) {
    container.addKey(id, CryptoFactory.newPKUtil().encrypt(dhPair.getPrivate(), pbk, payloadKey));
  }

  private PublicKey retrievePbk(String id) {
    throw new UnsupportedOperationException();
  }

  public byte[] encryptAndSign(byte[] key, Event event) {
    var unsignedPayload = CryptoFactory.newAESUtil().encrypt(key, event.serialize(), false);
    return CryptoFactory.newPKUtil().sign(unsignedPayload, signPair.getPrivate());
  }

  public EventContainer getContainer() {
    CryptoFactory.erase(payloadKey);
    return container;
  }
}
