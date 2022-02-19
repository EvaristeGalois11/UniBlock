package it.unifi.nave;

import it.unifi.nave.crypto.CryptoFactory;
import it.unifi.nave.data.BlockHeader;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public class Main {
  public static void main(String[] args) {
    var keyPair = CryptoFactory.newPKUtil().generateSignKeyPair();
    var test = "test";
    System.out.println("Stringa da firmare: " + test);
    var sign = CryptoFactory.newPKUtil().sign(test.getBytes(StandardCharsets.UTF_8), keyPair.getPrivate());
    System.out.println(sign.length);
    System.out.println("Firma: " + Base64.getEncoder().encodeToString(sign));
    var confirm = CryptoFactory.newPKUtil().verify(sign, keyPair.getPublic());
    System.out.println("Esito verifica: " + confirm);
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
