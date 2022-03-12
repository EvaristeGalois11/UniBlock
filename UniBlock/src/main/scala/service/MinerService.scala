package it.unifi.nave.uniblock
package service

import data.block.Block
import service.MinerService.{EXECUTOR, NUMBER_OF_CORE, NUMBER_OF_HASH, PROGRESS}

import java.util.concurrent.{ExecutorCompletionService, Executors, Future}

object MinerService {
  private val PROGRESS = "."
  private val NUMBER_OF_CORE = Runtime.getRuntime.availableProcessors
  private val NUMBER_OF_HASH = 500000
  private val EXECUTOR = Executors.newFixedThreadPool(NUMBER_OF_CORE)

  def apply(block: Block): Unit = new MinerService(block).mine()

  def terminate(): Unit = EXECUTOR.shutdownNow()
}

class MinerService(val block: Block, val progress: Boolean = true) {
  private val service = new ExecutorCompletionService[Option[Int]](EXECUTOR)
  private var miners: List[Future[Option[Int]]] = _

  def mine(): Unit = {
    miners = kickStart()
    block.blockHeader.nonce = LazyList.from(NUMBER_OF_CORE).flatMap(checkResult).head
    miners.foreach(_.cancel(true))
  }

  def checkResult(offset: Int): Option[Int] = {
    val result = service.take().get()
    if (progress && offset % 2 == 0) print(PROGRESS)
    if (result.isEmpty) miners = service.submit(createMiner(offset)) :: miners
    result
  }

  def kickStart(): List[Future[Option[Int]]] = (0 until NUMBER_OF_CORE).map(createMiner).map(service.submit).toList

  def createMiner(offset: Int): Miner = Miner(block.blockHeader.clone(), offset * NUMBER_OF_HASH, (offset + 1) * NUMBER_OF_HASH)
}
