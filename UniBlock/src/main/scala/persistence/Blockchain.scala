package it.unifi.nave.uniblock
package persistence

import data.{Block, EventContainer}

trait Blockchain extends Iterable[Block] {
  def saveBlock(block: Block): Unit

  def retrieveBlock(hash: String): Option[Block]

  def retrieveGenesisBlock(): Block

  def retrieveLastBlock(): Block

  def saveEvent(event: EventContainer, blockHash: String): Unit

  def retrieveEvent(hash: String): Option[EventContainer]

  override def iterator: Iterator[Block] = new BlockchainIterator(this)
}

class BlockchainIterator(private val blockchainPersistence: Blockchain) extends Iterator[Block] {
  private var current: Block = blockchainPersistence.retrieveLastBlock()

  override def hasNext: Boolean = current != null

  override def next(): Block = {
    val buffer = current
    current = blockchainPersistence.retrieveBlock(current.blockHeader.previousHash).orNull
    buffer
  }
}
