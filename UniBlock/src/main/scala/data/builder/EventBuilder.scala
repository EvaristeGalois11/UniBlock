package it.unifi.nave.uniblock
package data.builder

import crypto.{AESHelper, PKHelper}
import data.EventContainer
import data.event.Event
import persistence.PersistenceFactory

class EventBuilder(private val event: Event, private val author: String) {
  private val payloadKey = AESHelper.randomKey()
  private val payload = AESHelper.encrypt(payloadKey, Left(event.serialize), derive = false)
  private val sign = PKHelper.sign(Right(payload), PersistenceFactory.keyManager.retrieveSignPk())
  private val eventContainer = EventContainer(author, event.getType, payload, sign)
  addKey(author)

  def addKey(id: String): Unit = {
    val pbk = PersistenceFactory.searchCertificate(id).dhPbk
    val dhPk = PersistenceFactory.keyManager.retrieveDhPk()
    eventContainer.addKey(id, PKHelper.encrypt(dhPk, pbk, Left(payloadKey)))
  }

  def build: EventContainer = eventContainer
}
