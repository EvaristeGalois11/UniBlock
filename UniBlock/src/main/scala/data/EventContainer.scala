package it.unifi.nave.uniblock
package data

import data.event.Event.EventType


case class EventContainer(author: String, eventType: EventType, payload: String, sign: String) extends Hashable {
  private var _mapOfKeys: Map[String, String] = Map.empty

  def addKey(id: String, key: String): Unit = {
    _mapOfKeys += (id -> key)
  }

  def mapOfKeys: Map[String, String] = _mapOfKeys

}
