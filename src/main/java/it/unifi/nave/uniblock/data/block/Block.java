package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.data.event.Event;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Block {
  private final BlockHeader blockHeader;
  private final List<? extends Event> events;

  public Block(String previousHash, int difficulty, List<? extends Event> events, String rootHash) {
    this.events = events;
    blockHeader = new BlockHeader(previousHash, difficulty, rootHash);
  }

  public BlockHeader getBlockHeader() {
    return blockHeader;
  }

  public Collection<Event> getEvents() {
    return Collections.unmodifiableCollection(events);
  }
}
