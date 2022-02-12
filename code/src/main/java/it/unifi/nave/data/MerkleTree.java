package it.unifi.nave.data;

import com.google.common.collect.Lists;

import java.util.List;

// TODO Creare una vera struttura ad albero
public class MerkleTree {
  private List<Event> events;

  public MerkleTree(List<Event> events) {
    this.events = events;
  }

  public String getRootHash() {
    return getRootHash(events.stream().map(Hashable::hash).toList());
  }

  private String getRootHash(List<String> hashes) {
    if (hashes.size() == 1) {
      return hashes.get(0);
    } else {
      return getRootHash(Lists.partition(hashes, 2).stream().map(this::reduceHash).toList());
    }
  }

  private String reduceHash(List<String> hashes) {
    if (hashes.size() == 1) {
      hashes.add(hashes.get(0));
    }
    String doubleHash = hashes.get(0) + hashes.get(1);
    return HashUtil.hash(doubleHash);
  }
}
