package it.unifi.nave.uniblock
package helper.crypto

import java.security.SecureRandom

object RandomHelper {
  private val SECURE_RANDOM = new SecureRandom

  def generateRandom(length: Int): Array[Byte] = {
    val random = new Array[Byte](length)
    SECURE_RANDOM.nextBytes(random)
    random
  }

}
