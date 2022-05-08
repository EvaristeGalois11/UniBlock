package it.unifi.nave.uniblock.persistence;

import java.security.PrivateKey;
import java.util.Optional;

public interface KeyManager {
    void saveDhPk(String id, PrivateKey pk);

    void saveSignPk(String id, PrivateKey pk);

    Optional<PrivateKey> retrieveDhPk(String id);

    Optional<PrivateKey> retrieveSignPk(String id);
}
