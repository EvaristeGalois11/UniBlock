package it.unifi.nave.uniblock.service.factory;

import dagger.Binds;
import dagger.Module;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.persistence.impl.InMemoryPersistence;

@Module
public interface PersistenceModule {
  @Binds
  Blockchain blockchain(InMemoryPersistence inMemoryPersistence);

  @Binds
  KeyManager keyManager(InMemoryPersistence inMemoryPersistence);
}
