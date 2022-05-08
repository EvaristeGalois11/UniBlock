package it.unifi.nave.uniblock.persistence;

import it.unifi.nave.uniblock.persistence.impl.InMemoryPersistence;

public class PersistenceManager {
    private static final InMemoryPersistence IN_MEMORY_PERSISTENCE = new InMemoryPersistence();

    public static Blockchain getBlockchain() {
        return IN_MEMORY_PERSISTENCE;
    }

    public static KeyManager getKeyManager() {
        return IN_MEMORY_PERSISTENCE;
    }
}
