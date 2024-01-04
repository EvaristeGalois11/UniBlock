/*
 *Copyright (C) 2022-2024 Claudio Nave
 *
 *This file is part of UniBlock.
 *
 *UniBlock is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *UniBlock is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with UniBlock. If not, see <https://www.gnu.org/licenses/>.
 */
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
          .addOption("p", "progress", false, "Show the progress of the mining")
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
