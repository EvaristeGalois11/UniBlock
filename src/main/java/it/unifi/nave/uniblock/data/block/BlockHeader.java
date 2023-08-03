package it.unifi.nave.uniblock.data.block;

import java.io.Serializable;
import java.time.Instant;

public class BlockHeader implements Serializable, Cloneable {
  private final String previousHash;
  private final int difficulty;
  private final String rootHash;
  private final Instant timestamp = Instant.now();

  private int nonce = 0;

  public BlockHeader(String previousHash, int difficulty, String rootHash) {
    this.previousHash = previousHash;
    this.difficulty = difficulty;
    this.rootHash = rootHash;
  }

  @Override
  public BlockHeader clone() {
    try {
      return (BlockHeader) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public String getRootHash() {
    return rootHash;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public int getNonce() {
    return nonce;
  }

  public void setNonce(int nonce) {
    this.nonce = nonce;
  }
}
