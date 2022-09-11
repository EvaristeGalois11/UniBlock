package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.service.factory.DaggerHashServiceFactory;

import java.io.Serializable;
import java.time.Instant;

public class BlockHeader implements Serializable, Cloneable {
  private final String previousHash;
  private final int difficulty;
  private String rootHash = "";
  private Instant timestamp = Instant.now();
  private int nonce = 0;

  public BlockHeader(String previousHash, int difficulty) {
    this.previousHash = previousHash;
    this.difficulty = difficulty;
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

  public void setRootHash(String rootHash) {
    this.rootHash = rootHash;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public int getNonce() {
    return nonce;
  }

  public void setNonce(int nonce) {
    this.nonce = nonce;
  }
}
