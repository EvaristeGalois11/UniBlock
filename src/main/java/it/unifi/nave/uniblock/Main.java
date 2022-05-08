package it.unifi.nave.uniblock;

public class Main {
    public static void main(String[] args) {
        int difficulty = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        Demo demo = new Demo(difficulty);
        demo.startDemo();
    }
}
