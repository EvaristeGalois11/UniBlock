package it.unifi.nave.uniblock
package data.block

import data.event.Event
import helper.StringHelper

class Block(previousHash: String, difficulty: Int) {
  private val _blockHeader = new BlockHeader(previousHash, difficulty)
  private var _events: List[Event] = List.empty

  def blockHeader: BlockHeader = _blockHeader

  def events: List[Event] = _events

  def addEvent(event: Event): Unit = {
    addEvents(event :: Nil)
  }

  def addEvents(events: List[Event]): Unit = {
    _events ++= events
    _blockHeader.rootHash = MerkleTree.rootHash(_events)
  }

  def mine(): Unit = while (!_blockHeader.isMined) _blockHeader.incrementNonce()

  override def toString: String = {
    s"""${StringHelper.formatTitle("Block Header")}
       |$blockHeader
       |${StringHelper.formatTitle("Events")}
       |$eventsToString
       |${StringHelper.emptyLine}""".stripMargin
  }

  private def eventsToString: String = events.map(_.toString).mkString(s"\n${StringHelper.emptyLine}\n")
}
