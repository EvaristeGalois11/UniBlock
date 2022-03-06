package it.unifi.nave.uniblock

import crypto.PKHelper
import data.event.{Certificate, EventBuilder, ExamResult}
import data.{Block, EventContainer}
import persistence.PersistenceManager

import java.security.PrivateKey
import java.time.{LocalDate, Month}

object Main extends App {
  private val difficulty = 6

  println("Generazione blocco di genesi")
  val genesisHash = initGenesis
  println("Generazione di professore e studente")
  val certificateHash = createUsers(genesisHash)
  println("Pubblicazione voto appello")
  publishResult(certificateHash._1, certificateHash._2, certificateHash._3)

  private def initGenesis: String = {
    val block = new Block("GENESIS_BLOCK", difficulty)
    val genesisDhPk = PKHelper.generateDhKeyPair
    val genesisSignPk = PKHelper.generateSignKeyPair
    val genesisEventContainer = Certificate.build(genesisSignPk.getPublic, genesisDhPk.getPublic, genesis = true)
    registerPk(genesisSignPk.getPrivate, genesisDhPk.getPrivate, "")
    block.addEvent(genesisEventContainer)
    block.mine()
    PersistenceManager.blockchain.saveBlock(block)
    println(block)
    block.blockHeader.hash
  }

  private def createUsers(previousHash: String): (String, String, String) = {
    val professorEvent = createProfessor
    val studentEvent = createStudent
    val block = new Block(previousHash, difficulty)
    block.addEvents(professorEvent :: studentEvent :: Nil)
    block.mine()
    PersistenceManager.blockchain.saveBlock(block)
    println(block)
    (professorEvent.author, studentEvent.author, block.blockHeader.hash)
  }

  private def createProfessor: EventContainer = {
    val professorDhPk = PKHelper.generateDhKeyPair
    val professorSignPk = PKHelper.generateSignKeyPair
    val professorCertificate = Certificate.build(professorSignPk.getPublic, professorDhPk.getPublic)
    registerPk(professorSignPk.getPrivate, professorDhPk.getPrivate, professorCertificate.author)
    professorCertificate
  }

  private def createStudent: EventContainer = {
    val studentDhPk = PKHelper.generateDhKeyPair
    val studentSignPk = PKHelper.generateSignKeyPair
    val studentCertificate = Certificate.build(studentSignPk.getPublic, studentDhPk.getPublic)
    registerPk(studentSignPk.getPrivate, studentDhPk.getPrivate, studentCertificate.author)
    studentCertificate
  }

  private def registerPk(signPk: PrivateKey, dhPk: PrivateKey, id: String): Unit = {
    PersistenceManager.keyManager.saveSignPk(id, signPk)
    PersistenceManager.keyManager.saveDhPk(id, dhPk)
  }

  def publishResult(professor: String, student: String, certificateHash: String): Unit = {
    val event = ExamResult(professor, student, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), 30)
    val block = new Block(certificateHash, difficulty)
    val eventContainer = EventBuilder.buildContainer(event, professor, student :: Nil)
    block.addEvent(eventContainer)
    block.mine()
    PersistenceManager.blockchain.saveBlock(block)
    println(block)
  }

}
