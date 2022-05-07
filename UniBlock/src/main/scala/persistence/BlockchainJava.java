package it.unifi.nave.uniblock.persistence;

import it.unifi.nave.uniblock.data.block.BlockJava;

import java.util.Iterator;
import java.util.Optional;

public interface BlockchainJava extends Iterable<BlockJava> {
    void saveBlock(BlockJava block);

    Optional<BlockJava> retrieveBlock(String hash);

    BlockJava retrieveGenesisBlock();

    BlockJava retrieveLastBlock();

    @Override
    default Iterator<BlockJava> iterator() {
        return new BlockchainIteratorJava(this);
    }

    class BlockchainIteratorJava implements Iterator<BlockJava> {
        private final BlockchainJava blockchainPersistence;
        private BlockJava current;

        public BlockchainIteratorJava(BlockchainJava blockchainPersistence) {
            this.blockchainPersistence = blockchainPersistence;
            current = blockchainPersistence.retrieveLastBlock();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public BlockJava next() {
            var buffer = current;
            current = blockchainPersistence.retrieveBlock(current.getBlockHeader().getPreviousHash()).orElse(null);
            return buffer;
        }
    }
}
