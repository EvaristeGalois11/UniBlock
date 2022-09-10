package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.factory.DaggerDemoFactory;

public class Main {
  public static void main(String[] args) {
    int difficulty = args.length > 0 ? Integer.parseInt(args[0]) : 5;
    Demo demo = DaggerDemoFactory.create().get();
    demo.startDemo(difficulty);
  }
}
