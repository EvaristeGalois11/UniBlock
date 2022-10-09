package it.unifi.nave.uniblock.service.demo;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.block.BlockHeader;
import it.unifi.nave.uniblock.service.crypto.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Singleton
public class MinerService {
  private static final String PROGRESS = ".";
  private static final int NUMBER_OF_HASH = 500000;

  private final HashService hashService;
  private final PrintService printService;

  private Block block;
  private boolean progress;

  @Inject
  public MinerService(HashService hashService, PrintService printService) {
    this.hashService = hashService;
    this.printService = printService;
  }

  public void mine(Block block, boolean progress) {
    this.block = block;
    this.progress = progress;
    block.getBlockHeader().setNonce(mineNonce());
  }

  private int mineNonce() {
    return IntStream.iterate(0, i -> i + 1)
        .mapToObj(this::findNonce)
        .peek(o -> printProgress())
        .flatMapToInt(OptionalInt::stream)
        .findAny()
        .orElseThrow();
  }

  private void printProgress() {
    if (progress) {
      printService.print(PROGRESS);
    }
  }

  public OptionalInt findNonce(int offset) {
    return IntStream.range(offset * NUMBER_OF_HASH, (offset + 1) * NUMBER_OF_HASH)
        .parallel()
        .filter(n -> checkNonce(n, block.getBlockHeader().clone()))
        .findAny();
  }

  private boolean checkNonce(int i, BlockHeader blockHeader) {
    blockHeader.setNonce(i);
    return isMined(blockHeader);
  }

  private boolean isMined(BlockHeader blockHeader) {
    return hashService.hash(blockHeader).startsWith("0".repeat(blockHeader.getDifficulty()));
  }
}
