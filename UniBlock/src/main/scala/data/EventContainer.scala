package it.unifi.nave.uniblock
package data

import data.EventType.EventType

class EventContainer(val author: String,
                     val eventType: EventType,
                     private var _mapOfKeys: Map[String, Array[Byte]] = Map.empty,
                     var payload: Array[Byte] = Array.empty) extends Hashable {

  def addKey(id: String, key: Array[Byte]): Unit = {
    _mapOfKeys += (id -> key)
  }

  def mapOfKeys: Map[String, Array[Byte]] = _mapOfKeys

}

object EventType extends Enumeration {
  type EventType = Value
  val Genesis, PublicKeyCertificate, ExamResult = Value
}
