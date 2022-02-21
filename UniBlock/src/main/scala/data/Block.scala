package it.unifi.nave.uniblock
package data


// TODO Rivedere classe
class Block(var blockHeader: BlockHeader, var eventsNum: Int, var eventContainers: List[EventContainer]) {

  //  def this(previousHash: String, difficulty: Int) {
  //    blockHeader = new BlockHeader(previousHash, difficulty)
  //  }

  def addEvent(eventContainer: EventContainer): Unit = {
    addEvents(eventContainer :: Nil)
    updateRootHash()
  }

  def addEvents(eventContainers: List[EventContainer]): Unit = {
    this.eventContainers ++= eventContainers
    eventsNum = eventContainers.size
    updateRootHash()
  }

  private def updateRootHash(): Unit = {
    val merkleTree = new MerkleTree(eventContainers)
    blockHeader.rootHash = merkleTree.rootHash
  }

  def mine(): Unit = {
    while ( {
      !blockHeader.isMined
    }) blockHeader.incrementNonce()
  }

}
