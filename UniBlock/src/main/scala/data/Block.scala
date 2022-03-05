package it.unifi.nave.uniblock
package data

class Block(previousHash: String, difficulty: Int) {
  private val _blockHeader = new BlockHeader(previousHash, difficulty)
  private var _eventContainers: List[EventContainer] = List.empty

  def blockHeader: BlockHeader = _blockHeader

  def eventContainers: List[EventContainer] = _eventContainers

  def addEvent(eventContainer: EventContainer): Unit = {
    addEvents(eventContainer :: Nil)
  }

  def addEvents(eventContainers: List[EventContainer]): Unit = {
    _eventContainers ++= eventContainers
    _blockHeader.rootHash = MerkleTree.rootHash(_eventContainers)
  }

  def mine(): Unit = while (!_blockHeader.isMined) _blockHeader.incrementNonce()

  override def toString = s"Block($blockHeader, $eventContainers)"
}
