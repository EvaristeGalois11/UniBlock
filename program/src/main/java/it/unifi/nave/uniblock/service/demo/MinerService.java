package it.unifi.nave.uniblock.service.demo;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.block.BlockHeader;
import it.unifi.nave.uniblock.service.crypto.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Singleton
public class MinerService {
  private static final String PROGRESS = ".";
  private static final int NUMBER_OF_CORE = Runtime.getRuntime().availableProcessors();
  private static final int NUMBER_OF_HASH = 500000;

  private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(NUMBER_OF_CORE);
  private final HashService hashService;

  private CompletionService<OptionalInt> service;
  private List<Future<OptionalInt>> miners;

  private Block block;
  private boolean progress;

  @Inject
  public MinerService(HashService hashService) {
    this.hashService = hashService;
  }

  public void mine(Block block, boolean progress) {
    setUp(block, progress);
    start();
    cleanUp();
  }

  public void terminate() {
    EXECUTOR.shutdownNow();
  }

  private void setUp(Block block, boolean progress) {
    this.block = block;
    this.progress = progress;
    service = new ExecutorCompletionService<>(EXECUTOR);
    miners = new ArrayList<>();
  }

  private void start() {
    kickStart();
    block.getBlockHeader().setNonce(mineNonce());
  }

  private void cleanUp() {
    miners.forEach(f -> f.cancel(true));
  }

  private int mineNonce() {
    return IntStream.iterate(NUMBER_OF_CORE, i -> i + 1)
        .mapToObj(this::checkResult)
        .flatMapToInt(OptionalInt::stream)
        .findAny()
        .orElseThrow();
  }

  private OptionalInt checkResult(int offset) {
    try {
      OptionalInt result = service.take().get();
      if (progress && offset % 2 == 0) {
        System.out.print(PROGRESS);
      }
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
    return new Miner(block.getBlockHeader().clone(), offset, hashService);
  }

  private record Miner(BlockHeader blockHeader, int offset, HashService hashService)
      implements Callable<OptionalInt> {
    @Override
    public OptionalInt call() {
      return IntStream.range(offset * NUMBER_OF_HASH, (offset + 1) * NUMBER_OF_HASH)
          .filter(i -> !Thread.interrupted())
          .filter(this::checkNonce)
          .findAny();
    }

    private boolean checkNonce(int i) {
      blockHeader.setNonce(i);
      return isMined();
    }

    private boolean isMined() {
      return hashService.hash(blockHeader).startsWith("0".repeat(blockHeader.getDifficulty()));
    }
  }
}
