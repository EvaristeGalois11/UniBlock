package it.unifi.nave.uniblock
package persistence

import data.Block

trait Blockchain extends Iterable[Block] {
  def saveBlock(block: Block): Unit

  def retrieveBlock(hash: String): Option[Block]

  // TODO Rivedere questo metodo
  def retrieveGenesisBlock(): Block

  def retrieveLastBlock(): Block

  override def iterator: Iterator[Block] = new BlockchainIterator(this)

  private class BlockchainIterator(private val blockchainPersistence: Blockchain) extends Iterator[Block] {
    private var current: Block = blockchainPersistence.retrieveLastBlock()

    override def hasNext: Boolean = current != null

    override def next(): Block = {
      val buffer = current
      current = blockchainPersistence.retrieveBlock(current.blockHeader.previousHash).orNull
      buffer
    }
  }
}
