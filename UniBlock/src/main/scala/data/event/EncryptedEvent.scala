package it.unifi.nave.uniblock
package data.event

import data.event.Encryptable.EventType
import helper.StringHelper
import persistence.PersistenceManager

import it.unifi.nave.uniblock.helper.crypto.{AESHelper, PKHelper}

case class EncryptedEvent(author: String, eventType: EventType, payload: String, sign: String) extends Event {
  private var _mapOfKeys: Map[String, String] = Map.empty

  def addKey(id: String, key: String): Unit = {
    addKey(id -> key)
  }

  def addKey(key: (String, String)): Unit = {
    _mapOfKeys += key
  }

  def mapOfKeys: Map[String, String] = _mapOfKeys

  override def toString: String = {
    s"""${StringHelper.formatLeft(hash, "hash")}
       |${StringHelper.formatLeft(author, "author")}
       |${StringHelper.formatLeft(eventType, "eventType")}
       |$mapOfKeysToString
       |${StringHelper.formatLeft(payload, "encrypted event")}
       |${StringHelper.formatLeft(sign, "signature")}""".stripMargin
  }

  private def mapOfKeysToString: String = {
    mapOfKeys.map(f => StringHelper.formatLeft(f._1, "receiver") + "\n" + StringHelper.formatLeft(f._2,"key")).mkString("\n")
  }
}

object EncryptedEvent {
  def apply(event: Encryptable, author: String, receivers: List[String]): EncryptedEvent = {
    val payloadKey = AESHelper.randomKey()
    val payload = AESHelper.encrypt(payloadKey, Left(event.serialize), derive = false)
    val sign = PKHelper.sign(Right(payload), PersistenceManager.keyManager.retrieveSignPk(author).get)
    val eventContainer = EncryptedEvent(author, event.getType, payload, sign)
    (author :: receivers).map(id => encryptKey(id, payloadKey, author)).foreach(eventContainer.addKey)
    eventContainer
  }

  private def encryptKey(id: String, key: Array[Byte], author: String): (String, String) = {
    val pbk = PersistenceManager.searchCertificate(id).dhPbk
    val dhPk = PersistenceManager.keyManager.retrieveDhPk(author).get
    id -> PKHelper.encrypt(dhPk, pbk, Left(key))
  }
}
