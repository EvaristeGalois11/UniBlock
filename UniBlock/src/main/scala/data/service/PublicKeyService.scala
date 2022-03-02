package it.unifi.nave.uniblock
package data.service

import data.EventType.PublicKeyCertificate
import data.event.PublicKeyCertificate
import persistence.Blockchain

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.security.PublicKey

class PublicKeyService(private val blockchain: Blockchain) {

  def searchPbk(id: String): PublicKey = {

    blockchain
      .flatMap(_.eventContainers)
      .find(e => e.eventType == PublicKeyCertificate && e.author == id)
      .map(e => deserializePbk(e.payload))


    null
  }

  private def deserializePbk(payload: Array[Byte]): PublicKeyCertificate = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(payload))
    val event = ois.readObject.asInstanceOf[PublicKeyCertificate]
    ois.close()
    event
  }

  private def verifyCertificate(certificate: PublicKeyCertificate): Boolean = {
    val genesisKey = retrieveGenesisKey();
    // TODO Cambiare oggetto
    ???
//    PKHelper.verify(Base64.getDecoder.decode(certificate.sign), genesisKey);
  }

  private def retrieveGenesisKey(): PublicKey = {
    blockchain.retrieveGenesisBlock().eventContainers.find(_.eventType == PublicKeyCertificate).map(e => deserializePbk(e.payload)).get.pbk
  }

}
