package it.unifi.nave.uniblock
package service

import data.block.Block
import service.MinerService.{EXECUTOR, NUMBER_OF_CORE, NUMBER_OF_HASH}

import java.util.concurrent.{ExecutorCompletionService, Executors}

object MinerService {
  private val NUMBER_OF_CORE = Runtime.getRuntime.availableProcessors
  private val NUMBER_OF_HASH = 100000
  private val EXECUTOR = Executors.newFixedThreadPool(NUMBER_OF_CORE)

  def apply(block: Block): Unit = new MinerService(block).mine()

  def terminate(): Unit = EXECUTOR.shutdownNow()
}

class MinerService(val block: Block) {
  private val service = new ExecutorCompletionService[Option[Int]](EXECUTOR)

  def mine(): Unit = {
    kickStart()
    block.blockHeader.nonce = LazyList.from(NUMBER_OF_CORE).flatMap(checkResult).head
  }

  def checkResult(offset: Int): Option[Int] = {
    val result = service.take().get()
    if (result.isEmpty) {
      service.submit(createMiner(offset))
    }
    result
  }

  def kickStart(): Unit = (0 until NUMBER_OF_CORE).map(createMiner).foreach(service.submit)

  def createMiner(offset: Int): Miner = Miner(block.blockHeader.clone(), offset * NUMBER_OF_HASH, (offset + 1) * NUMBER_OF_HASH)
}
