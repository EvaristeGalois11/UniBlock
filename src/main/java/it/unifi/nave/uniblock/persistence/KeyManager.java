package it.unifi.nave.uniblock.persistence;

import java.security.PrivateKey;

public interface KeyManager {
  void saveDhPk(String id, PrivateKey pk);

  void saveSignPk(String id, PrivateKey pk);

  PrivateKey retrieveDhPk(String id);

  PrivateKey retrieveSignPk(String id);
}
