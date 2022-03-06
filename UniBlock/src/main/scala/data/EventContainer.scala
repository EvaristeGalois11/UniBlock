package it.unifi.nave.uniblock
package data

import data.event.Event.EventType

// TODO Differenziare i container per i certificati
case class EventContainer(author: String, eventType: EventType, payload: String, sign: String) extends Hashable {
  private var _mapOfKeys: Map[String, String] = Map.empty

  def addKey(id: String, key: String): Unit = {
    addKey(id -> key)
  }

  def addKey(key: (String, String)): Unit = {
    _mapOfKeys += key
  }

  def mapOfKeys: Map[String, String] = _mapOfKeys

  override def toString: String = {
    s"""hash = $hash
       |author = $author
       |eventType = $eventType
       |payload = $payload
       |sign = $sign
       |$mapOfKeysToString""".stripMargin
  }

  private def mapOfKeysToString: String = mapOfKeys.map(f => s"Receiver = ${f._1} -> Key = ${f._2}").mkString("\n")
}
