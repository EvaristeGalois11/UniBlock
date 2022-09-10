package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.factory.DaggerDemoFactory;

public class Main {
  public static void main(String[] args) {
    int difficulty = args.length > 0 ? Integer.parseInt(args[0]) : 5;
    Demo demo = buildDemo(difficulty);
    demo.startDemo();
  }

  private static Demo buildDemo(int difficulty) {
    return DaggerDemoFactory.create().get();
  }
}
