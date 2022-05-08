package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.helper.StringHelperJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BlockHeaderJava implements Serializable, Cloneable {
    private final String previousHash;
    private final int difficulty;
    private String rootHash = "";
    private Instant timestamp = Instant.now();
    private int nonce = 0;

    public BlockHeaderJava(String previousHash, int difficulty) {
        this.previousHash = previousHash;
        this.difficulty = difficulty;
    }

    public boolean isMined() {
        return HashHelperJava.hash(this).startsWith("0".repeat(difficulty));
    }

    @Override
    public String toString() {
        return StringHelperJava.formatLeft(HashHelperJava.hash(this), "hash") + "\n"
                + StringHelperJava.formatLeft(previousHash, "previousHash") + "\n"
                + StringHelperJava.formatLeft(difficulty, "difficulty") + "\n"
                + StringHelperJava.formatLeft(rootHash, "rootHash") + "\n"
                + StringHelperJava.formatLeft(timestampToString(), "timestamp") + "\n"
                + StringHelperJava.formatLeft(nonce, "nonce");
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
