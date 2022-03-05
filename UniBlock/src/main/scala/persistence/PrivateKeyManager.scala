package it.unifi.nave.uniblock
package persistence

import java.security.PrivateKey

trait PrivateKeyManager {
  def saveDhPk(pk: PrivateKey): Unit

  def saveSignPk(pk: PrivateKey): Unit

  def retrieveDhPk(): PrivateKey

  def retrieveSignPk(): PrivateKey
}
