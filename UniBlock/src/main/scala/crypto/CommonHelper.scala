package it.unifi.nave.uniblock
package crypto

import java.security.SecureRandom
import java.util

object CommonHelper {
  private val SECURE_RANDOM = new SecureRandom

  def generateRandom(length: Int): Array[Byte] = {
    val random = new Array[Byte](length)
    SECURE_RANDOM.nextBytes(random)
    random
  }

  def erase(arrays: Array[Byte]*): Unit = {
    for (array <- arrays) {
      util.Arrays.fill(array, 0.toByte)
    }
  }

}
