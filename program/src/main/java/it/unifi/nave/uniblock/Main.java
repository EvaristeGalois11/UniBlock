package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.service.demo.DemoService;
import it.unifi.nave.uniblock.service.demo.factory.DaggerDemoServiceFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
  private static final Options OPTIONS =
      new Options()
          .addOption("d", "difficulty", true, "Choose the difficulty of the mining")
          .addOption("p", "progress", false, "Show progress of mining")
          .addOption("h", "help", false, "Print this message");

  public static void main(String[] args) throws ParseException {
    CommandLine cmd = new DefaultParser().parse(OPTIONS, args);
    if (cmd.hasOption("h")) {
      printHelp();
    } else {
      startDemo(cmd);
    }
  }

  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("uniblock", OPTIONS, true);
  }

  private static void startDemo(CommandLine cmd) {
    int difficulty = Integer.parseInt(cmd.getOptionValue("d", "5"));
    boolean progress = cmd.hasOption("p");
    DemoService demoService = DaggerDemoServiceFactory.create().get();
    demoService.startDemo(difficulty, progress);
  }
}
