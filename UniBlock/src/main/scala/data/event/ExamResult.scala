package it.unifi.nave.uniblock
package data.event

import data.event.Encryptable.EventType

import java.time.LocalDate

case class ExamResult(professor: String, student: String, codeExam: String,
                      date: LocalDate, result: Int) extends Encryptable {
  override def getType: EventType = Encryptable.ExamResult
}
