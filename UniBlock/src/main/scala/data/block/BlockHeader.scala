package it.unifi.nave.uniblock
package data.block

import data.Hashable
import helper.StringHelper

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}

class BlockHeader(val previousHash: String, val difficulty: Int) extends Hashable with Cloneable {
  private var _rootHash: String = ""
  private var _timestamp: Instant = Instant.now
  private var _nonce: Int = 0

  def timestamp: Instant = _timestamp

  def nonce: Int = _nonce

  def nonce_=(nonce: Int): Unit = _nonce = nonce

  def rootHash: String = _rootHash

  def rootHash_=(rootHash: String): Unit = _rootHash = rootHash

  def incrementNonce(): Unit =
    if (Integer.MAX_VALUE == _nonce) {
      _timestamp = Instant.now;
      _nonce = 0
    } else {
      _nonce += 1
    }

  def isMined: Boolean = hash.startsWith("0".repeat(difficulty))

  override def toString: String = {
    s"""${StringHelper.formatLeft(hash, "hash")}
       |${StringHelper.formatLeft(previousHash, "previousHash")}
       |${StringHelper.formatLeft(difficulty, "difficulty")}
       |${StringHelper.formatLeft(rootHash, "rootHash")}
       |${StringHelper.formatLeft(timestampToString, "timestamp")}
       |${StringHelper.formatLeft(nonce, "nonce")}""".stripMargin
  }

  private def timestampToString = timestamp.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss"))

  override def clone(): BlockHeader = super.clone().asInstanceOf[BlockHeader]
}
