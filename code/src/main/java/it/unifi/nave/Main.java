package it.unifi.nave;

import it.unifi.nave.data.BlockHeader;

import java.time.Duration;
import java.time.Instant;

public class Main {
  public static void main(String[] args) {
  }

  private static void testMining() {
    BlockHeader blockHeader = new BlockHeader("temp", 10);
    Instant start = Instant.now();
    mining(blockHeader);
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    System.out.println("BlockHeader mined in " + duration.toMinutes() + "m " + duration.toSecondsPart() + "s");
    System.out.println("BlockHeader hash: " + blockHeader.hash());
  }

  private static void mining(BlockHeader blockHeader) {
    while (!blockHeader.isMined()) {
      blockHeader.incrementNonce();
    }
  }
}
