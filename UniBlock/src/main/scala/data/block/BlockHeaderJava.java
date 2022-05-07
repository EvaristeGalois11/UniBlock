package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.helper.StringHelper;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BlockHeaderJava implements Cloneable {
    private final String previousHash;
    private final int difficulty;
    private String rootHash = "";
    private Instant timestamp = Instant.now();
    private int nonce = 0;

    public BlockHeaderJava(String previousHash, int difficulty) {
        this.previousHash = previousHash;
        this.difficulty = difficulty;
    }

    public void incrementNonce() {
        if (Integer.MAX_VALUE == nonce) {
            timestamp = Instant.now();
            nonce = 0;
        } else {
            nonce += 1;
        }
    }

    public boolean isMined() {
        return HashHelperJava.hash(this).startsWith("0".repeat(difficulty));
    }

    @Override
    public String toString() {
        return StringHelper.formatLeft(HashHelperJava.hash(this), "hash")
                + StringHelper.formatLeft(previousHash, "previousHash")
                + StringHelper.formatLeft(difficulty, "difficulty")
                + StringHelper.formatLeft(rootHash, "rootHash")
                + StringHelper.formatLeft(timestampToString(), "timestamp")
                + StringHelper.formatLeft(nonce, "nonce");
    }

    private String timestampToString() {
        return timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public BlockHeaderJava clone() {
        try {
            return (BlockHeaderJava) super.clone();
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
