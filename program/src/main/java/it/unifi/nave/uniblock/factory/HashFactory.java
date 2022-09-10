package it.unifi.nave.uniblock.factory;

import dagger.Component;
import it.unifi.nave.uniblock.helper.HashHelper;

import javax.inject.Singleton;

@Singleton
@Component
public interface HashFactory {
  HashHelper get();
}
