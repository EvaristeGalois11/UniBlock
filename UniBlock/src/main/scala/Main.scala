package it.unifi.nave.uniblock

import crypto.PKHelper
import data.block.Block
import data.event.{Certificate, EncryptedEvent, ExamResult}
import persistence.PersistenceManager

import java.security.PrivateKey
import java.time.{LocalDate, Month}

object Main extends App {
  val difficulty = args match {
    case Array(difficulty) => difficulty.toInt
    case _ => 5
  }

  println("Generazione blocco di genesi")
  val genesisHash = initGenesis
  println()
  println("Generazione di professore e studente")
  val certificateHash = createUsers(genesisHash)
  println()
  println("Pubblicazione voto appello")
  publishResult(certificateHash._1, certificateHash._2, certificateHash._3)

  private def initGenesis: String = {
    val block = new Block("GENESIS_BLOCK", difficulty)
    val genesisDhPk = PKHelper.generateDhKeyPair
    val genesisSignPk = PKHelper.generateSignKeyPair
    val genesisCertificate = Certificate("", genesisSignPk.getPublic, genesisDhPk.getPublic, Certificate.Genesis)
    registerPk(genesisSignPk.getPrivate, genesisDhPk.getPrivate, Certificate.GENESIS)
    block.addEvent(genesisCertificate)
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
    (professorEvent.userId, studentEvent.userId, block.blockHeader.hash)
  }

  private def createProfessor: Certificate = {
    val professorDhPk = PKHelper.generateDhKeyPair
    val professorSignPk = PKHelper.generateSignKeyPair
    val professorCertificate = Certificate("Lorenzo Bettini", professorSignPk.getPublic, professorDhPk.getPublic, Certificate.Professor)
    registerPk(professorSignPk.getPrivate, professorDhPk.getPrivate, professorCertificate.userId)
    professorCertificate
  }

  private def createStudent: Certificate = {
    val studentDhPk = PKHelper.generateDhKeyPair
    val studentSignPk = PKHelper.generateSignKeyPair
    val studentCertificate = Certificate("Claudio Nave", studentSignPk.getPublic, studentDhPk.getPublic, Certificate.Student)
    registerPk(studentSignPk.getPrivate, studentDhPk.getPrivate, studentCertificate.userId)
    studentCertificate
  }

  private def registerPk(signPk: PrivateKey, dhPk: PrivateKey, id: String): Unit = {
    PersistenceManager.keyManager.saveSignPk(id, signPk)
    PersistenceManager.keyManager.saveDhPk(id, dhPk)
  }

  private def publishResult(professor: String, student: String, certificateHash: String): Unit = {
    val event = ExamResult(professor, student, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), 30)
    val block = new Block(certificateHash, difficulty)
    val eventContainer = EncryptedEvent(event, professor, student :: Nil)
    block.addEvent(eventContainer)
    block.mine()
    PersistenceManager.blockchain.saveBlock(block)
    println(block)
  }

}
