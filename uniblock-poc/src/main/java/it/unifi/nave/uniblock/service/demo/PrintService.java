package it.unifi.nave.uniblock.service.demo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PrintService {
  @Inject
  public PrintService() {}

  public void println(String str) {
    print(str + "\n");
  }

  public void print(String str) {
    System.out.print(str);
  }
}
