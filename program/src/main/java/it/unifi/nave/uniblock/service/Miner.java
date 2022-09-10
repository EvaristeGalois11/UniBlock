package it.unifi.nave.uniblock.service;

import it.unifi.nave.uniblock.data.block.BlockHeader;

import java.util.Optional;
import java.util.concurrent.Callable;

public record Miner(BlockHeader blockHeader, int start, int end)
    implements Callable<Optional<Integer>> {
  @Override
  public Optional<Integer> call() {
    Integer result = null;
    for (int i = start; i < end && !Thread.interrupted(); i++) {
      blockHeader.setNonce(i);
      if (blockHeader.isMined()) {
        result = i;
        break;
      }
    }
    return Optional.ofNullable(result);
  }
}
