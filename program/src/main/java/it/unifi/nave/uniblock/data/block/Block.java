package it.unifi.nave.uniblock.data.block;

import com.google.common.collect.Lists;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.service.factory.DaggerHashServiceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Block {
  private final BlockHeader blockHeader;
  private final List<Event> events;

  public Block(String previousHash, int difficulty) {
    blockHeader = new BlockHeader(previousHash, difficulty);
    events = new ArrayList<>();
  }

  public void addEvents(List<Event> events) {
    this.events.addAll(events);
    blockHeader.setRootHash(calculateRootHash(this.events));
  }

  private String calculateRootHash(List<Event> events) {
    return getRootHash(events.stream().map(DaggerHashServiceFactory.create().get()::hash).toList());
  }

  private String getRootHash(List<String> hashes) {
    if (hashes.size() == 1) {
      return hashes.get(0);
    } else {
      return getRootHash(Lists.partition(hashes, 2).stream().map(this::reduceHash).toList());
    }
  }

  private String reduceHash(List<String> hashes) {
    var first = hashes.get(0);
    var second = hashes.size() == 2 ? hashes.get(1) : first;
    return DaggerHashServiceFactory.create().get().hash(first + second);
  }

  public BlockHeader getBlockHeader() {
    return blockHeader;
  }

  public Collection<Event> getEvents() {
    return Collections.unmodifiableCollection(events);
  }
}
