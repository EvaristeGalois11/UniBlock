package it.unifi.nave.data;

import java.security.NoSuchAlgorithmException;

public class Main {
  public static void main(String[] args) throws NoSuchAlgorithmException {
    BlockHeader blockHeader = new BlockHeader("temp", 5);
    mining(blockHeader);
    System.out.println("BlockHeader mined: " + blockHeader.hash());
  }

  public static void mining(BlockHeader blockHeader) {
    while (!blockHeader.isMined()) {
      blockHeader.incrementNonce();
    }
  }
}
