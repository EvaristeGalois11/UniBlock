package it.unifi.nave.uniblock
package crypto

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.HexFormat

object HashHelper {
  private val HASH_TYPE = "SHA3-256"

  def hash(string: String): String = hash(string.getBytes(StandardCharsets.UTF_8))

  def hash(bytes: Array[Byte]): String = HexFormat.of.formatHex(hashRaw(bytes))

  def hashRaw(string: String): Array[Byte] = hashRaw(string.getBytes(StandardCharsets.UTF_8))

  def hashRaw(bytes: Array[Byte]): Array[Byte] = MessageDigest.getInstance(HASH_TYPE).digest(bytes)
}
