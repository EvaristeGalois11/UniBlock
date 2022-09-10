package it.unifi.nave.uniblock.service.factory;

import dagger.Component;
import it.unifi.nave.uniblock.service.demo.DemoService;

import javax.inject.Singleton;

@Singleton
@Component(modules = PersistenceModule.class)
public interface DemoServiceFactory {
  DemoService get();
}
