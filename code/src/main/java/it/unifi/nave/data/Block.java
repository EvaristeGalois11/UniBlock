package it.unifi.nave.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block {
  private BlockHeader blockHeader;
  private int eventsNum;
  private List<Event> events;

  public Block(String previousHash, int difficulty) {
    blockHeader = new BlockHeader(previousHash, difficulty);
    events = new ArrayList<>();
  }

  public void addEvent(Event event) {
    addEvents(Collections.singletonList(event));
  }

  public void addEvents(List<Event> events) {
    this.events.addAll(events);
    eventsNum = events.size();
    updateRootHash();
  }

  private void updateRootHash() {
    MerkleTree merkleTree = new MerkleTree(events);
    blockHeader.setRootHash(merkleTree.getRootHash());
  }

  public void mine() {
    while (!blockHeader.isMined()) {
      blockHeader.incrementNonce();
    }
  }
}
