package it.unifi.nave.uniblock
package data.block

import data.Hashable
import helper.StringHelper

import java.time.Instant

class BlockHeader(val previousHash: String, val difficulty: Int, var rootHash: String = "") extends Hashable {
  private var _timestamp: Instant = Instant.now
  private var _nonce: Int = 0

  def timestamp: Instant = _timestamp

  def nonce: Int = _nonce

  def incrementNonce(): Unit =
    if (Integer.MAX_VALUE == _nonce) {
      _timestamp = Instant.now;
      _nonce = 0
    } else {
      _nonce += 1
      if (nonce % 1000000 == 0) println(nonce)
    }

  def isMined: Boolean = hash.startsWith("0".repeat(difficulty))

  override def toString: String = {
    s"""${StringHelper.formatLeft(hash, "hash")}
       |${StringHelper.formatLeft(previousHash, "previousHash")}
       |${StringHelper.formatLeft(difficulty, "difficulty")}
       |${StringHelper.formatLeft(rootHash, "rootHash")}
       |${StringHelper.formatLeft(timestamp, "timestamp")}
       |${StringHelper.formatLeft(nonce, "nonce")}""".stripMargin
  }
}
