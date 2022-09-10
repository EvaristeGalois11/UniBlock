package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.factory.DaggerHashFactory;
import it.unifi.nave.uniblock.helper.StringHelper;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

  public boolean isMined() {
    return DaggerHashFactory.create().get().hash(this).startsWith("0".repeat(difficulty));
  }

  @Override
  public String toString() {
    return StringHelper.formatLeft(DaggerHashFactory.create().get().hash(this), "hash")
        + "\n"
        + StringHelper.formatLeft(previousHash, "previousHash")
        + "\n"
        + StringHelper.formatLeft(difficulty, "difficulty")
        + "\n"
        + StringHelper.formatLeft(rootHash, "rootHash")
        + "\n"
        + StringHelper.formatLeft(timestampToString(), "timestamp")
        + "\n"
        + StringHelper.formatLeft(nonce, "nonce");
  }

  private String timestampToString() {
    return timestamp
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
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
