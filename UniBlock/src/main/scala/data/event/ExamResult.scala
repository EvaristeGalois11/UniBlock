package it.unifi.nave.uniblock
package data.event

import java.time.Instant

class ExamResult(var professor: String, var student: String, var codeExam: String,
                 var instant: Instant, var result: Int) extends Event {

}
