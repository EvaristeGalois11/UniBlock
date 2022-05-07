package it.unifi.nave.uniblock.service;

import it.unifi.nave.uniblock.data.block.BlockHeaderJava;

import java.util.Optional;
import java.util.concurrent.Callable;

public record MinerJava(BlockHeaderJava blockHeader, int start, int end) implements Callable<Optional<Integer>> {
    @Override
    public Optional<Integer> call() {
        Integer result = null;
        for (int i = start; i < end && !Thread.interrupted(); i++) {
            blockHeader.setNonce(i);
            if (blockHeader.isMined()) {
                result = i;
            }
        }
        return Optional.ofNullable(result);
    }
}
