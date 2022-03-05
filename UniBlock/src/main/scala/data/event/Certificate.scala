package it.unifi.nave.uniblock
package data.event

import crypto.{HashHelper, PKHelper}
import data.EventContainer
import data.event.Event.EventType
import persistence.PersistenceFactory

import java.security.PublicKey
import java.util.Base64

case class Certificate(signPbk: PublicKey, dhPbk: PublicKey) extends Event {
  override def getType: EventType = Event.Certificate
}

object Certificate {
  def build(signPbk: PublicKey, dhPbk: PublicKey): EventContainer = {
    val certificate = Certificate(signPbk, dhPbk)
    val payload = Base64.getEncoder.encodeToString(certificate.serialize)
    val sign = PKHelper.sign(Right(payload), PersistenceFactory.keyManager.retrieveSignPk())
    val id = HashHelper.hash(Left(certificate.serialize))
    EventContainer(id, Event.Certificate, payload, sign)
  }
}
