package it.unifi.nave.uniblock.persistence.impl;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.helper.crypto.HashHelper;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPersistence implements Blockchain, KeyManager {
    private final Map<String, Block> blockchain = new HashMap<>();
    private Block genesisBlock;
    private Block lastBlock;
    private final Map<String, PrivateKey> dhPk = new HashMap<>();
    private final Map<String, PrivateKey> signPk = new HashMap<>();

    @Override
    public void saveBlock(Block block) {
        if (genesisBlock == null) {
            genesisBlock = block;
        }
        lastBlock = block;
        blockchain.put(HashHelper.hash(block.getBlockHeader()), block);
    }

    @Override
    public Optional<Block> retrieveBlock(String hash) {
        return Optional.ofNullable(blockchain.get(hash));
    }

    @Override
    public Block retrieveGenesisBlock() {
        return genesisBlock;
    }

    @Override
    public Block retrieveLastBlock() {
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
