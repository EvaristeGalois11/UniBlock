package it.unifi.nave.uniblock
package data.event

import crypto.{HashHelper, PKHelper}
import data.EventContainer
import data.event.Event.EventType
import persistence.PersistenceManager

import java.security.PublicKey
import java.util.Base64

case class Certificate(signPbk: PublicKey, dhPbk: PublicKey) extends Event {
  override def getType: EventType = Event.Certificate
}

object Certificate {
  def build(signPbk: PublicKey, dhPbk: PublicKey, genesis: Boolean = false): EventContainer = {
    val certificate = Certificate(signPbk, dhPbk)
    val payload = Base64.getEncoder.encodeToString(certificate.serialize)
    val sign = if (genesis) "" else PKHelper.sign(Right(payload), PersistenceManager.keyManager.retrieveSignPk("").get)
    val id = if (genesis) "" else HashHelper.hash(Left(certificate.serialize))
    EventContainer(id, if (genesis) Event.Genesis else Event.Certificate, payload, sign)
  }
}
