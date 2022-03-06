package it.unifi.nave.uniblock
package data.event

import data.event.Event.EventType

import java.time.LocalDate

case class ExamResult(professor: String, student: String, codeExam: String,
                      date: LocalDate, result: Int) extends Event {
  override def getType: EventType = Event.ExamResult
}
