package it.unifi.nave.uniblock

import data.Block
import data.event.Certificate

import java.time.{Duration, Instant}

object Main extends App {

  testMining()

  private def initGenesis(): Unit = {
    val block = new Block("test", 5)
    val genesis = Certificate.build(null, null)
  }

  private def testMining(): Unit = {
    val block = new Block("", 6)
    println(s"Block: $block")
    val start = Instant.now
    block.mine()
    val end = Instant.now
    val duration = Duration.between(start, end)
    println(s"Block mined in ${duration.toMinutes}m ${duration.toSecondsPart}s")
    println(s"Block: $block")
  }

}
