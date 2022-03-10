package it.unifi.nave.uniblock
package helper

import helper.StringHelper.PADDING

object StringHelper {
  val LINE_LENGTH = 100
  val PADDING = "-"
  val MARGIN = 5
  val FIELD_LENGTH = 20

  def emptyLine: String = PADDING.repeat(LINE_LENGTH)

  def formatCenter(string: String): String = {
    val normalized = if (string.length % 2 == 0) string else string + PADDING
    val toFill = LINE_LENGTH - string.length
    PADDING.repeat(toFill / 2) + normalized + PADDING.repeat(toFill / 2)
  }

  def formatTitle(title: String): String = {
    s"""$emptyLine
       |${formatCenter(s" ${title.toUpperCase} ")}
       |$emptyLine""".stripMargin
  }

  def formatLeft(any: Any, label: String): String = formatLeft(any.toString, label)

  def formatLeft(string: String, label: String): String = {
    val margin = PADDING.repeat(MARGIN)
    val fieldName = s"$margin${(" " + label + " ").padTo(FIELD_LENGTH, PADDING(0))} = "
    val leftMargin = PADDING.repeat(fieldName.length - 1) + " "
    val realLine = LINE_LENGTH - fieldName.length - MARGIN - 1
    val rightMargin = " " + (if (string.length % realLine != 0) PADDING.repeat(realLine - string.length % realLine) else "") + margin
    string.grouped(realLine).mkString(fieldName, s" $margin\n$leftMargin", rightMargin)
  }
}
