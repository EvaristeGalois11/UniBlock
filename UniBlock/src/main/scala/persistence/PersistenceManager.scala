package it.unifi.nave.uniblock
package persistence

import crypto.PKHelper
import data.event.Certificate
import persistence.impl.InMemoryPersistence

import java.security.PublicKey

object PersistenceManager {
  private val persistenceManager = InMemoryPersistence

  def blockchain: Blockchain = persistenceManager

  def keyManager: KeyManager = persistenceManager

  def searchCertificate(userId: String): Certificate = blockchain
    .flatMap(_.events)
    .flatMap {
      case certificate: Certificate => Some(certificate)
      case _ => None
    }
    .find(_.userId == userId)
    .filter(verifyCertificate)
    .get

  private def verifyCertificate(certificate: Certificate): Boolean = certificate match {
    case Certificate(_, _, Certificate.Genesis, _, _, _) => true
    case Certificate(_, _, _, signPbk, dhPbk, sign) => verify(signPbk, dhPbk, sign)
  }

  private def verify(signPbk: PublicKey, dhPbk: PublicKey, sign: String): Boolean = {
    PKHelper.verify(Left(signPbk.getEncoded ++ dhPbk.getEncoded), sign, searchGenesisCertificate.signPbk)
  }

  def searchGenesisCertificate: Certificate = searchCertificate(Certificate.GENESIS)
}
