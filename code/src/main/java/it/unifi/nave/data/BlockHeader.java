package it.unifi.nave.data;

import java.time.Instant;

public class BlockHeader extends Hashable {
  private final String previousHash;
  private final int difficulty;
  private Instant timestamp;
  private String rootHash;
  private int nonce;

  public BlockHeader(String previousHash, int difficulty) {
    this.previousHash = previousHash;
    this.difficulty = difficulty;
    timestamp = Instant.now();
    rootHash = "";
    nonce = 0;
  }

  public void incrementNonce() {
    if (Integer.MAX_VALUE == nonce) {
      System.out.println("all nonce tried, resetting timestamp");
      timestamp = Instant.now();
      nonce = 0;
    } else {
      System.out.println("nonce tried: " + nonce);
      ++nonce;
    }
  }

  public boolean isMined() {
    return hash().startsWith("0".repeat(difficulty));
  }

  public void setRootHash(String rootHash) {
    this.rootHash = rootHash;
  }
}
