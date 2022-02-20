package it.unifi.nave.uniblock
package data.builder

import crypto.{AESHelper, RandomHelper, PKHelper}
import data.EventContainer
import data.event.Event

import java.security.{KeyPair, PublicKey}

class EventBuilder(var dhPair: KeyPair, var signPair: KeyPair, var container: EventContainer, var payloadKey: Array[Byte]) {

  //  def this(event: Event) {
  //    this()
  //    initKeyPair()
  //    payloadKey = CommonHelper.generateRandom(16)
  //    encryptPayload(event)
  //  }

  private def initKeyPair(): Unit = ???

  private def encryptPayload(event: Event): Unit = {
    val idAuthor = retrieveIdUser
    container = new EventContainer(idAuthor)
    container.payload = encryptAndSign(payloadKey, event)
    addKey(idAuthor, dhPair.getPublic)
  }

  private def retrieveIdUser: String = ???

  def addKey(id: String): Unit = {
    addKey(id, retrievePbk(id))
  }

  def addKey(id: String, pbk: PublicKey): Unit = {
    container.addKey(id, PKHelper.encrypt(dhPair.getPrivate, pbk, payloadKey))
  }

  private def retrievePbk(id: String): PublicKey = ???

  def encryptAndSign(key: Array[Byte], event: Event): Array[Byte] = {
    val unsignedPayload = AESHelper.encrypt(key, event.serialize, false)
    PKHelper.sign(unsignedPayload, signPair.getPrivate)
  }
}
