package it.unifi.nave.uniblock.factory;

import dagger.Component;
import it.unifi.nave.uniblock.Demo;

import javax.inject.Singleton;

@Singleton
@Component(modules = BlockchainModule.class)
public interface DemoFactory {
  Demo get();
}
