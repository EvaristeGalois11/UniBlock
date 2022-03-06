package it.unifi.nave.uniblock
package persistence

import java.security.PrivateKey

trait KeyManager {
  def saveDhPk(id: String, pk: PrivateKey): Unit

  def saveSignPk(id: String, pk: PrivateKey): Unit

  def retrieveDhPk(id: String): Option[PrivateKey]

  def retrieveSignPk(id: String): Option[PrivateKey]
}
