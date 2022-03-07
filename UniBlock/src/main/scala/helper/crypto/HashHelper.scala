package it.unifi.nave.uniblock
package helper.crypto

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.HexFormat

object HashHelper {
  private val HASH_TYPE = "SHA3-256"

  def hash(toHash: Either[Array[Byte], String]): String = {
    HexFormat.of.formatHex(hashRaw(toHash))
  }

  def hashRaw(toHash: Either[Array[Byte], String]): Array[Byte] = {
    val bytes = toHash match {
      case Left(value) => value
      case Right(value) => value.getBytes(StandardCharsets.UTF_8)
    }
    MessageDigest.getInstance(HASH_TYPE).digest(bytes)
  }
}
