package it.unifi.nave.uniblock
package data.event

import data.event.Event.EventType

import java.time.Instant

case class ExamResult(professor: String, student: String, codeExam: String,
                      instant: Instant, result: Int) extends Event {
  override def getType: EventType = Event.Certificate
}
