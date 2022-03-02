package it.unifi.nave.uniblock
package data.builder

import crypto.{AESHelper, PKHelper}
import data.EventContainer
import data.EventType.EventType
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

  private def encryptPayload(event: Event, eventType: EventType): Unit = {
    val idAuthor = retrieveIdUser
    container = new EventContainer(idAuthor, eventType)
    // TODO Cambiare payload
//    container.payload = encryptAndSign(payloadKey, event)
    addKey(idAuthor, dhPair.getPublic)
  }

  private def retrieveIdUser: String = ???

  def addKey(id: String): Unit = {
    addKey(id, retrievePbk(id))
  }

  def addKey(id: String, pbk: PublicKey): Unit = {
    // TODO Cambiare mappa in stringhe
//    container.addKey(id, PKHelper.encrypt(dhPair.getPrivate, pbk, Left(payloadKey)))
  }

  private def retrievePbk(id: String): PublicKey = ???

  def encryptAndSign(key: Array[Byte], event: Event): String = {
    val unsignedPayload = AESHelper.encrypt(key, Left(event.serialize), derive = false)
    PKHelper.sign(Right(unsignedPayload), signPair.getPrivate) + unsignedPayload
  }
}
