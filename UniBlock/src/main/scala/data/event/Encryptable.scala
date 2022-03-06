package it.unifi.nave.uniblock
package data.event

import data.Hashable
import data.event.Encryptable.EventType

trait Encryptable extends Hashable {
  def getType: EventType
}

object Encryptable extends Enumeration {
  type EventType = Value
  val Genesis, Certificate, ExamResult = Value
}
