package it.unifi.nave.uniblock
package persistence.impl

import data.{Block, EventContainer}
import persistence.{Blockchain, PrivateKeyManager}

import java.security.PrivateKey

object InMemoryPersistence extends Blockchain with PrivateKeyManager {
  private var blockchain: Map[String, Block] = Map.empty
  private var events: Map[String, EventContainer] = Map.empty
  private var genesisBlock: Block = _
  private var lastBlock: Block = _
  private var dhPk: PrivateKey = _
  private var signPk: PrivateKey = _

  override def saveBlock(block: Block): Unit = {
    lastBlock = block
    blockchain += (block.blockHeader.hash -> block)
  }

  override def retrieveBlock(hash: String): Option[Block] = blockchain.get(hash)

  override def retrieveGenesisBlock(): Block = genesisBlock

  override def retrieveLastBlock(): Block = lastBlock

  override def saveEvent(event: EventContainer, blockHash: String): Unit = {
    events += (event.hash -> event)
    blockchain.get(blockHash).foreach(b => b.addEvent(event))
  }

  override def retrieveEvent(hash: String): Option[EventContainer] = events.get(hash)

  override def saveDhPk(pk: PrivateKey): Unit = dhPk = pk

  override def saveSign(pk: PrivateKey): Unit = signPk = pk

  override def retrieveDhPk(): PrivateKey = dhPk

  override def retrieveSignPk(): PrivateKey = signPk
}
