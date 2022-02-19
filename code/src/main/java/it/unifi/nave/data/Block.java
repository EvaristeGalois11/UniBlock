package it.unifi.nave.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block {
  private BlockHeader blockHeader;
  private int eventsNum;
  private List<EventContainer> eventContainers;

  public Block(String previousHash, int difficulty) {
    blockHeader = new BlockHeader(previousHash, difficulty);
    eventContainers = new ArrayList<>();
  }

  public void addEvent(EventContainer eventContainer) {
    addEvents(Collections.singletonList(eventContainer));
  }

  public void addEvents(List<EventContainer> eventContainers) {
    this.eventContainers.addAll(eventContainers);
    eventsNum = eventContainers.size();
    updateRootHash();
  }

  private void updateRootHash() {
    MerkleTree merkleTree = new MerkleTree(eventContainers);
    blockHeader.setRootHash(merkleTree.getRootHash());
  }

  public void mine() {
    while (!blockHeader.isMined()) {
      blockHeader.incrementNonce();
    }
  }
}
