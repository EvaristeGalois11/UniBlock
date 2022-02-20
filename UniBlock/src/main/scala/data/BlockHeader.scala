package it.unifi.nave.uniblock
package data

import java.time.Instant

class BlockHeader(val previousHash: String,
                  val difficulty: Int,
                  var timestamp: Instant = Instant.now,
                  var rootHash: String = "",
                  var nonce: Int = 0) extends Hashable {

  def incrementNonce(): Unit =
    if (Integer.MAX_VALUE == nonce) {
      timestamp = Instant.now;
      nonce = 0
    } else {
      nonce += 1
    }

  def isMined: Boolean = hash.startsWith("0".repeat(difficulty))
}
