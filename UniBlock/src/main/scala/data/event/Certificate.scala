package it.unifi.nave.uniblock
package data.event

import crypto.{HashHelper, PKHelper}
import data.event.Certificate.CertificateType
import helper.StringHelper
import persistence.PersistenceManager

import java.security.PublicKey
import java.util.Base64

case class Certificate(userId: String, name: String, certificateType: CertificateType, signPbk: PublicKey, dhPbk: PublicKey, sign: String) extends Event {

  override def toString: String = {
    s"""${StringHelper.formatLeft(hash, "hash")}
       |${StringHelper.formatLeft(userId, "userId")}
       |${StringHelper.formatLeft(name, "name")}
       |${StringHelper.formatLeft(certificateType, "certificateType")}
       |${keyToString(signPbk, "sign")}
       |${keyToString(dhPbk, "dh")}
       |${StringHelper.formatLeft(sign, "signature")}""".stripMargin
  }

  private def keyToString(pbk: PublicKey, label: String): String = {
    s"""${StringHelper.formatLeft(Base64.getEncoder.encodeToString(pbk.getEncoded), s"$label public key")}""".stripMargin
  }
}

object Certificate extends Enumeration {
  val GENESIS = "GENESIS"
  type CertificateType = Value
  val Professor, Student, Genesis = Value

  def apply(name: String, signPbk: PublicKey, dhPbk: PublicKey, certificateType: CertificateType): Certificate = certificateType match {
    case Genesis => Certificate(GENESIS, GENESIS, Genesis, signPbk, dhPbk, GENESIS)
    case _ => Certificate(calculateUserId(signPbk, dhPbk), name, certificateType, signPbk, dhPbk, authorizedKey(signPbk, dhPbk))
  }

  private def calculateUserId(signPbk: PublicKey, dhPbk: PublicKey): String = {
    HashHelper.hash(Left(signPbk.getEncoded ++ dhPbk.getEncoded))
  }

  private def authorizedKey(signPbk: PublicKey, dhPbk: PublicKey): String = {
    PKHelper.sign(Left(signPbk.getEncoded ++ dhPbk.getEncoded), PersistenceManager.keyManager.retrieveSignPk(GENESIS).get)
  }
}
