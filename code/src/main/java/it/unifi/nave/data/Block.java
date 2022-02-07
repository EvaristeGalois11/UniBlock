package it.unifi.nave.data;

import java.util.List;

public class Block {
  private BlockHeader blockHeader;
  private List<Event> events;

  public Block(String previousHash, int difficulty) {
    blockHeader = new BlockHeader(previousHash, difficulty);
  }

  public void addEvent(Event event) {
    events.add(event);
    blockHeader.setRootHash(getRootHash());
  }

  public void mine() {
    while (!blockHeader.isMined()) {
      blockHeader.incrementNonce();
    }
  }

  public String getRootHash() {
    // TODO Calcolare il root hash della lista di eventi
    throw new UnsupportedOperationException();
  }
}
