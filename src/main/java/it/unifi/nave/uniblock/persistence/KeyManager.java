package it.unifi.nave.uniblock.persistence;

import java.security.PrivateKey;
import java.util.Optional;

public interface KeyManager {
    void saveDhPk(String id, PrivateKey pk);

    void saveSignPk(String id, PrivateKey pk);

    PrivateKey retrieveDhPk(String id);

    PrivateKey retrieveSignPk(String id);
}
