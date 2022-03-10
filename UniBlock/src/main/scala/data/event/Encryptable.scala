package it.unifi.nave.uniblock
package data.event

import data.Hashable
import data.event.Encryptable.EventType

import java.time.LocalDate

object Encryptable extends Enumeration {
  type EventType = Value
  val Genesis, Certificate, ExamPublishing, ExamBooking, ExamResult, ExamConfirm = Value
}

sealed trait Encryptable extends Hashable {
  def getType: EventType
}

case class ExamPublishing(professor: String, codeExam: String, date: LocalDate) extends Encryptable {
  override def getType: EventType = Encryptable.ExamPublishing
}

case class ExamBooking(student: String, codeExam: String, date: LocalDate) extends Encryptable {
  override def getType: EventType = Encryptable.ExamBooking
}

case class ExamResult(professor: String, student: String, codeExam: String, date: LocalDate, result: Int) extends Encryptable {
  override def getType: EventType = Encryptable.ExamResult
}

case class ExamConfirm(student: String, codeExam: String, date: LocalDate, confirm: Boolean) extends Encryptable {
  override def getType: EventType = Encryptable.ExamConfirm
}
