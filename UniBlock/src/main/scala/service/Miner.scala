package it.unifi.nave.uniblock
package service

import data.block.BlockHeader

import java.util.concurrent.Callable

case class Miner(blockHeader: BlockHeader, start: Int, end: Int) extends Callable[Option[Int]] {
  private var interrupted = false

  override def call(): Option[Int] =
    LazyList.range(start, end).map(setNonce).filterNot(_ => isInterrupted).find(_.isMined).map(_.nonce)

  private def setNonce(nonce: Int): BlockHeader = {
    blockHeader.nonce = nonce
    blockHeader
  }

  private def isInterrupted: Boolean = {
    interrupted = Thread.interrupted()
    interrupted
  }
}
