/*
 *Copyright (C) 2022-2024 Claudio Nave
 *
 *This file is part of UniBlock.
 *
 *UniBlock is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *UniBlock is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with UniBlock. If not, see <https://www.gnu.org/licenses/>.
 */
package it.unifi.nave.uniblock.persistence.impl;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.service.crypto.HashService;
import it.unifi.nave.uniblock.service.crypto.PKService;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InMemoryPersistence extends Blockchain implements KeyManager {
  private final Map<String, Block> blockchain = new HashMap<>();
  private Block genesisBlock;
  private Block lastBlock;
  private final Map<String, PrivateKey> dhPk = new HashMap<>();
  private final Map<String, PrivateKey> signPk = new HashMap<>();
  private final HashService hashService;

  @Inject
  public InMemoryPersistence(HashService hashService, PKService pkService) {
    super(pkService);
    this.hashService = hashService;
  }

  @Override
  public void saveBlock(Block block) {
    if (genesisBlock == null) {
      genesisBlock = block;
    }
    lastBlock = block;
    blockchain.put(hashService.hash(block.getBlockHeader()), block);
  }

  @Override
  public Block retrieveBlock(String hash) {
    return blockchain.get(hash);
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
  public PrivateKey retrieveDhPk(String id) {
    return dhPk.get(id);
  }

  @Override
  public PrivateKey retrieveSignPk(String id) {
    return signPk.get(id);
  }
}
