package it.unifi.nave.uniblock.service;

import it.unifi.nave.uniblock.data.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class MinerService {
  private static final String PROGRESS = ".";
  private static final int NUMBER_OF_CORE = Runtime.getRuntime().availableProcessors();
  private static final int NUMBER_OF_HASH = 500000;
  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(NUMBER_OF_CORE);
  private final Block block;
  private final boolean progress;
  private final CompletionService<Optional<Integer>> service;
  private final List<Future<Optional<Integer>>> miners;
  public MinerService(Block block, boolean progress) {
    this.block = block;
    this.progress = progress;
    service = new ExecutorCompletionService<>(EXECUTOR);
    miners = new ArrayList<>();
  }

  public static void mine(Block block) {
    new MinerService(block, true).mine();
  }

  public static void terminate() {
    EXECUTOR.shutdownNow();
  }

  public void mine() {
    kickStart();
    block.getBlockHeader().setNonce(mineNonce());
    miners.forEach(f -> f.cancel(true));
  }

  private int mineNonce() {
    return IntStream.iterate(NUMBER_OF_CORE, i -> i + 1)
        .mapToObj(this::checkResult)
        .flatMap(Optional::stream)
        .findAny()
        .orElseThrow();
  }

  private Optional<Integer> checkResult(int offset) {
    try {
      Optional<Integer> result = service.take().get();
      if (progress && offset % 2 == 0) System.out.print(PROGRESS);
      if (result.isEmpty()) {
        miners.add(service.submit(createMiner(offset)));
      }
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void kickStart() {
    IntStream.range(0, NUMBER_OF_CORE)
        .mapToObj(this::createMiner)
        .map(service::submit)
        .forEach(miners::add);
  }

  private Miner createMiner(int offset) {
    return new Miner(
        block.getBlockHeader().clone(), offset * NUMBER_OF_HASH, (offset + 1) * NUMBER_OF_HASH);
  }
}
