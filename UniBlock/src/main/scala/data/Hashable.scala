package it.unifi.nave.uniblock
package data

import crypto.HashHelper

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

trait Hashable extends Serializable {
  def hash: String = HashHelper.hash(serialize)

  def serialize: Array[Byte] = {
    val arrayOutputStream = new ByteArrayOutputStream
    val objectOutputStream = new ObjectOutputStream(arrayOutputStream)
    objectOutputStream.writeObject(this)
    arrayOutputStream.toByteArray
  }
}
