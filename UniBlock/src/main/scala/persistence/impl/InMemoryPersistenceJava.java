package it.unifi.nave.uniblock.persistence.impl;

import it.unifi.nave.uniblock.data.block.BlockJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;
import it.unifi.nave.uniblock.persistence.BlockchainJava;
import it.unifi.nave.uniblock.persistence.KeyManagerJava;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPersistenceJava implements BlockchainJava, KeyManagerJava {
    private final Map<String, BlockJava> blockchain = new HashMap<>();
    private BlockJava genesisBlock;
    private BlockJava lastBlock;
    private final Map<String, PrivateKey> dhPk = new HashMap<>();
    private final Map<String, PrivateKey> signPk = new HashMap<>();

    @Override
    public void saveBlock(BlockJava block) {
        if (genesisBlock == null) {
            genesisBlock = block;
        }
        lastBlock = block;
        blockchain.put(HashHelperJava.hash(block.getBlockHeader()), block);
    }

    @Override
    public Optional<BlockJava> retrieveBlock(String hash) {
        return Optional.of(blockchain.get(hash));
    }

    @Override
    public BlockJava retrieveGenesisBlock() {
        return genesisBlock;
    }

    @Override
    public BlockJava retrieveLastBlock() {
        return lastBlock;
    }

    @Override
    public void saveDhPk(String id, PrivateKey pk) {
        dhPk.put(id, pk);
    }

    @Override
    public void saveSignPk(String id, PrivateKey pk) {
        signPk.put(id, pk);
    }

    @Override
    public Optional<PrivateKey> retrieveDhPk(String id) {
        return Optional.of(dhPk.get(id));
    }

    @Override
    public Optional<PrivateKey> retrieveSignPk(String id) {
        return Optional.of(signPk.get(id));
    }
}
