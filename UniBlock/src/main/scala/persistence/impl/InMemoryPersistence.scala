package it.unifi.nave.uniblock
package persistence.impl

import persistence.{Blockchain, KeyManager}

import it.unifi.nave.uniblock.data.block.Block

import java.security.PrivateKey

object InMemoryPersistence extends Blockchain with KeyManager {
  private var blockchain: Map[String, Block] = Map.empty
  private var genesisBlock: Block = _
  private var lastBlock: Block = _
  private var dhPk: Map[String, PrivateKey] = Map.empty
  private var signPk: Map[String, PrivateKey] = Map.empty

  override def saveBlock(block: Block): Unit = {
    genesisBlock = if (genesisBlock == null) block else genesisBlock
    lastBlock = block
    blockchain += (block.blockHeader.hash -> block)
  }

  override def retrieveBlock(hash: String): Option[Block] = blockchain.get(hash)

  override def retrieveGenesisBlock(): Block = genesisBlock

  override def retrieveLastBlock(): Block = lastBlock

  override def saveDhPk(id: String, pk: PrivateKey): Unit = dhPk += id -> pk

  override def saveSignPk(id: String, pk: PrivateKey): Unit = signPk += id -> pk

  override def retrieveDhPk(id: String): Option[PrivateKey] = dhPk.get(id)

  override def retrieveSignPk(id: String): Option[PrivateKey] = signPk.get(id)
}
