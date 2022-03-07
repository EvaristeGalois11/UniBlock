package it.unifi.nave.uniblock
package data

import it.unifi.nave.uniblock.helper.crypto.HashHelper

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

trait Hashable extends Serializable {
  def hash: String = HashHelper.hash(Left(serialize))

  def serialize: Array[Byte] = {
    val arrayOutputStream = new ByteArrayOutputStream
    val objectOutputStream = new ObjectOutputStream(arrayOutputStream)
    objectOutputStream.writeObject(this)
    arrayOutputStream.toByteArray
  }
}
