package it.unifi.nave.uniblock.service.data;

import com.google.common.collect.Lists;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.service.crypto.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MerkleTreeService {
  private final HashService hashService;

  @Inject
  public MerkleTreeService(HashService hashService) {
    this.hashService = hashService;
  }

  public String calculateRootHash(List<? extends Event> events) {
    return getRootHash(events.stream().map(hashService::hash).toList());
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
    return hashService.hash(first + second);
  }
}
