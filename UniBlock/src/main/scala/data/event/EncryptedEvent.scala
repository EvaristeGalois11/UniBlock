package it.unifi.nave.uniblock
package data.event

import crypto.{AESHelper, PKHelper}
import data.event.Encryptable.EventType
import persistence.PersistenceManager

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
    s"""hash = $hash
       |author = $author
       |eventType = $eventType
       |$mapOfKeysToString
       |$payloadToString
       |sign = $sign""".stripMargin
  }

  private def mapOfKeysToString: String = mapOfKeys.map(f => s"Receiver = ${f._1} -> Key = ${f._2}").mkString("\n")

  private def payloadToString: String = {
    s"""-----BEGIN ENCRYPTED EVENT-----
       |${payload.grouped(72).mkString("\n")}
       |-----END ENCRYPTED EVENT-----""".stripMargin
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
