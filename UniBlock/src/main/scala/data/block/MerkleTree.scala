package it.unifi.nave.uniblock
package data.block

import crypto.HashHelper
import data.event.Event

import scala.annotation.tailrec

object MerkleTree {

  def rootHash(events: List[Event]): String = getRootHash(events.map(_.hash))

  @tailrec
  private def getRootHash(hashes: List[String]): String = hashes match {
    case rootHash :: Nil => rootHash
    case _ => getRootHash(hashes.grouped(2).map(reduceHash).toList)
  }

  @tailrec
  private def reduceHash(hashes: List[String]): String = hashes match {
    case singleHash :: Nil => reduceHash(singleHash :: singleHash :: Nil)
    case firstHash :: secondHash => HashHelper.hash(Right(firstHash + secondHash))
  }

}
