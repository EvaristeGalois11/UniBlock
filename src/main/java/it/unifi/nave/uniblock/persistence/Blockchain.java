package it.unifi.nave.uniblock.persistence;

import it.unifi.nave.uniblock.data.block.Block;

import java.util.Iterator;
import java.util.Optional;

public interface Blockchain extends Iterable<Block> {
    void saveBlock(Block block);

    Optional<Block> retrieveBlock(String hash);

    Block retrieveGenesisBlock();

    Block retrieveLastBlock();

    @Override
    default Iterator<Block> iterator() {
        return new BlockchainIteratorJava(this);
    }

    class BlockchainIteratorJava implements Iterator<Block> {
        private final Blockchain blockchainPersistence;
        private Block current;

        public BlockchainIteratorJava(Blockchain blockchainPersistence) {
            this.blockchainPersistence = blockchainPersistence;
            current = blockchainPersistence.retrieveLastBlock();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Block next() {
            var buffer = current;
            current = blockchainPersistence.retrieveBlock(current.getBlockHeader().getPreviousHash()).orElse(null);
            return buffer;
        }
    }
}
