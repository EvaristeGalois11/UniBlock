package it.unifi.nave.uniblock
package data.event

import data.Hashable
import data.event.Event.EventType

trait Event extends Hashable {
  def getType: EventType
}

object Event extends Enumeration {
  type EventType = Value
  val Genesis, Certificate, ExamResult = Value
}
