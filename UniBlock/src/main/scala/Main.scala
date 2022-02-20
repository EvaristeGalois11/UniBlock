package it.unifi.nave.uniblock

import crypto.PKHelper
import data.BlockHeader

import java.nio.charset.StandardCharsets
import java.time.{Duration, Instant}
import java.util.Base64

object Main extends App {

  val keyPair = PKHelper.generateSignKeyPair
  val test = "test"
  System.out.println("Stringa da firmare: " + test)
  val sign = PKHelper.sign(test.getBytes(StandardCharsets.UTF_8), keyPair.getPrivate)
  println(sign.length)
  System.out.println("Firma: " + Base64.getEncoder.encodeToString(sign))
  val confirm = PKHelper.verify(sign, keyPair.getPublic)
  System.out.println("Esito verifica: " + confirm)


  private def testMining(): Unit = {
    val blockHeader = new BlockHeader("temp", 10)
    val start = Instant.now
    mining(blockHeader)
    val end = Instant.now
    val duration = Duration.between(start, end)
    System.out.println("BlockHeader mined in " + duration.toMinutes + "m " + duration.toSecondsPart + "s")
    System.out.println("BlockHeader hash: " + blockHeader.hash)
  }

  private def mining(blockHeader: BlockHeader): Unit = while (!blockHeader.isMined) blockHeader.incrementNonce()

}
