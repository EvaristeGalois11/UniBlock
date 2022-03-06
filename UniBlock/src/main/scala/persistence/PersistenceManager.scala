package it.unifi.nave.uniblock
package persistence

import crypto.PKHelper
import data.EventContainer
import data.event.Event.EventType
import data.event.{Certificate, Event}
import persistence.impl.InMemoryPersistence

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.util.Base64

object PersistenceManager {
  private val persistenceManager = InMemoryPersistence

  def blockchain: Blockchain = persistenceManager

  def keyManager: KeyManager = persistenceManager

  def searchCertificate(id: String): Certificate = searchCertificate(id, Event.Certificate)

  private def searchCertificate(id: String, eventType: EventType): Certificate = blockchain
    .flatMap(_.eventContainers)
    .find(e => e.eventType == eventType && e.author == id)
    .map(deserializeCertificate)
    .get

  private def deserializeCertificate(eventContainer: EventContainer): Certificate = {
    if (!verifyCertificate(eventContainer)) throw new IllegalArgumentException("Can't verify certificate")
    val ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder.decode(eventContainer.payload)))
    ois.readObject.asInstanceOf[Certificate]
  }

  private def verifyCertificate(eventContainer: EventContainer): Boolean = eventContainer match {
    case EventContainer(_, Event.Genesis, _, _) => true
    case EventContainer(_, _, payload, sign) => PKHelper.verify(Right(payload), sign, searchGenesisCertificate.signPbk)
  }

  def searchGenesisCertificate: Certificate = searchCertificate("", Event.Genesis)
}
