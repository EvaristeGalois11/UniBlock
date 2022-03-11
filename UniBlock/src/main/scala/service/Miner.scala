package it.unifi.nave.uniblock
package service

import data.block.BlockHeader

import java.util.concurrent.Callable

case class Miner(blockHeader: BlockHeader, start: Int, end: Int) extends Callable[Option[Int]] {
  override def call(): Option[Int] = {
    blockHeader.nonce = start
    LazyList.range(start, end).takeWhile(_ => !(Thread.interrupted() || blockHeader.isMined)).foreach(blockHeader.nonce = _)
    if (blockHeader.isMined) Some(blockHeader.nonce) else None
  }
}
