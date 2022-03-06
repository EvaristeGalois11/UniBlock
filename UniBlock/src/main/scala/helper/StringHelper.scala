package it.unifi.nave.uniblock
package helper

object StringHelper {
  val LINE_LENGHT = 100
  val PADDING = "-"
  val LEFT_MARGIN = 5

  def emptyLine: String = {
    PADDING.repeat(LINE_LENGHT)
  }

  def formatCenter(string: String): String = {
    val normalized = if (string.length % 2 == 0) string else string + PADDING
    val toFill = LINE_LENGHT - string.length
    PADDING.repeat(toFill / 2) + normalized + PADDING.repeat(toFill / 2)
  }

  def formatTitle(title: String): String = {
    s"""$emptyLine
       |${formatCenter(title)}
       |$emptyLine""".stripMargin
  }

  def formatLeft(any: Any, name: String): String = {
    formatLeft(s"$name = $any")
  }

  def formatLeft(string: String): String = {
    PADDING.repeat(LEFT_MARGIN) + string + PADDING.repeat(LINE_LENGHT - LEFT_MARGIN - string.length)
  }

  def formatString(string: String): String = {
    val leftMargin = PADDING.repeat(LEFT_MARGIN)
    val realLine = LINE_LENGHT - LEFT_MARGIN
    val rightMargin = PADDING.repeat(realLine - string.length % realLine)
    string.grouped(LINE_LENGHT - LEFT_MARGIN).mkString(leftMargin, s"\n$leftMargin", rightMargin)
  }
}
