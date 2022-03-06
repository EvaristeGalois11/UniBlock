package it.unifi.nave.uniblock
package data.event

import crypto.{AESHelper, PKHelper}
import data.EventContainer
import persistence.PersistenceManager

object EventBuilder {
  def buildContainer(event: Event, author: String, receivers: List[String]): EventContainer = {
    val payloadKey = AESHelper.randomKey()
    val payload = AESHelper.encrypt(payloadKey, Left(event.serialize), derive = false)
    val sign = PKHelper.sign(Right(payload), PersistenceManager.keyManager.retrieveSignPk(author).get)
    val eventContainer = EventContainer(author, event.getType, payload, sign)
    (author :: receivers).map(id => encryptKey(id, payloadKey, author)).foreach(eventContainer.addKey)
    eventContainer
  }

  private def encryptKey(id: String, key: Array[Byte], author: String): (String, String) = {
    val pbk = PersistenceManager.searchCertificate(id).dhPbk
    val dhPk = PersistenceManager.keyManager.retrieveDhPk(author).get
    id -> PKHelper.encrypt(dhPk, pbk, Left(key))
  }
}
