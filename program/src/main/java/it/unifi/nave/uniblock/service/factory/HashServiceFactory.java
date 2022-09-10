package it.unifi.nave.uniblock.service.factory;

import dagger.Component;
import it.unifi.nave.uniblock.service.crypto.HashService;

import javax.inject.Singleton;

@Singleton
@Component
public interface HashServiceFactory {
  HashService get();
}
