package it.unifi.nave.uniblock
package data

import crypto.HashHelper

import scala.annotation.tailrec

// TODO Creare una vera struttura ad albero
class MerkleTree(private var eventContainers: List[EventContainer]) {

  def rootHash: String = getRootHash(eventContainers.map(_.hash))

  @tailrec
  private def getRootHash(hashes: List[String]): String = hashes match {
    case rootHash :: Nil => rootHash
    case _ => getRootHash(hashes.grouped(2).map(reduceHash).toList)
  }

  @tailrec
  private def reduceHash(hashes: List[String]): String = hashes match {
    case singleHash :: Nil => reduceHash(singleHash :: singleHash :: Nil)
    case firstHash :: secondHash => HashHelper.hash(firstHash + secondHash)
  }

}
