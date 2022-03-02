package it.unifi.nave.uniblock
package data.event

import java.security.PublicKey

class PublicKeyCertificate(val id: String, val pbk: PublicKey, val sign: String) extends Event {
}
