package it.unifi.nave.uniblock

import data.block.Block
import data.event._
import helper.crypto.PKHelper
import persistence.PersistenceManager
import service.MinerService

import java.security.PrivateKey
import java.time.{Duration, Instant, LocalDate, Month}

object Main extends App {
  val difficulty = args match {
    case Array(difficulty) => difficulty.toInt
    case _ => 5
  }

  val genesis = initGenesis
  val certificates = createUsers(genesis.blockHeader.hash)
  val professor = certificates.events.flatMap {
    case Certificate(userId, _, Certificate.Professor, _, _, _) => Some(userId)
    case _ => None
  } match {
    case professor :: Nil => professor
  }
  val students = certificates.events.flatMap {
    case Certificate(userId, _, Certificate.Student, _, _, _) => Some(userId)
    case _ => None
  } match {
    case student1 :: student2 :: Nil => (student1, student2)
  }
  val publishing = publishExam(professor, students, certificates.blockHeader.hash)
  val booking = bookExam(professor, students, publishing.blockHeader.hash)
  val result = publishResult(professor, students, booking.blockHeader.hash)
  val confirm = confirmExam(professor, students, result.blockHeader.hash)
  MinerService.terminate()

  private def initGenesis: Block = {
    val genesisDhPk = PKHelper.generateDhKeyPair
    val genesisSignPk = PKHelper.generateSignKeyPair
    val genesisCertificate = Certificate("", genesisSignPk.getPublic, genesisDhPk.getPublic, Certificate.Genesis)
    registerPk(genesisSignPk.getPrivate, genesisDhPk.getPrivate, Certificate.GENESIS)
    mineBlock("GENESIS_BLOCK", "Generating genesis block", genesisCertificate)
  }

  private def createUsers(previousHash: String): Block = {
    val professorEvent = createUser("Lorenzo Bettini", Certificate.Professor)
    val studentEvent1 = createUser("Claudio Nave", Certificate.Student)
    val studentEvent2 = createUser("Mario Rossi", Certificate.Student)
    mineBlock(previousHash, "Generating professor and students certificate", professorEvent, studentEvent1, studentEvent2)
  }

  private def createUser(name: String, certificateType: Certificate.CertificateType): Certificate = {
    val professorDhPk = PKHelper.generateDhKeyPair
    val professorSignPk = PKHelper.generateSignKeyPair
    val professorCertificate = Certificate(name, professorSignPk.getPublic, professorDhPk.getPublic, certificateType)
    registerPk(professorSignPk.getPrivate, professorDhPk.getPrivate, professorCertificate.userId)
    professorCertificate
  }

  private def registerPk(signPk: PrivateKey, dhPk: PrivateKey, id: String): Unit = {
    PersistenceManager.keyManager.saveSignPk(id, signPk)
    PersistenceManager.keyManager.saveDhPk(id, dhPk)
  }

  private def publishExam(professor: String, students: (String, String), previousHash: String): Block = {
    val event = ExamPublishing(professor, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29))
    val eventContainer = EncryptedEvent(event, professor, students._1 :: students._2 :: Nil)
    mineBlock(previousHash, "Publishing exam", eventContainer)
  }

  private def bookExam(professor: String, students: (String, String), previousHash: String): Block = {
    val event1 = ExamBooking(students._1, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29))
    val event2 = ExamBooking(students._2, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29))
    val eventContainer1 = EncryptedEvent(event1, students._1, professor :: Nil)
    val eventContainer2 = EncryptedEvent(event2, students._2, professor :: Nil)
    mineBlock(previousHash, "Booking exams", eventContainer1, eventContainer2)
  }

  private def publishResult(professor: String, students: (String, String), previousHash: String): Block = {
    val event1 = ExamResult(professor, students._1, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), 30)
    val event2 = ExamResult(professor, students._2, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), 25)
    val eventContainer1 = EncryptedEvent(event1, professor, students._1 :: Nil)
    val eventContainer2 = EncryptedEvent(event2, professor, students._2 :: Nil)
    mineBlock(previousHash, "Publishing results", eventContainer1, eventContainer2)
  }

  private def confirmExam(professor: String, students: (String, String), previousHash: String): Block = {
    val event1 = ExamConfirm(students._1, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), confirm = true)
    val event2 = ExamConfirm(students._2, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), confirm = false)
    val eventContainer1 = EncryptedEvent(event1, students._1, professor :: Nil)
    val eventContainer2 = EncryptedEvent(event2, students._2, professor :: Nil)
    mineBlock(previousHash, "Accepting exams", eventContainer1, eventContainer2)
  }

  private def mineBlock(previousHash: String, message: String, events: Event*): Block = {
    val block = new Block(previousHash, difficulty)
    block.addEvents(events.toList)
    print(message)
    val start = Instant.now()
    MinerService(block)
    val end = Instant.now()
    val duration = Duration.between(start, end)
    println(
    s"""
       |Block mined in ${duration.toMinutes}m ${duration.toSecondsPart}s
       |$block
       |""".stripMargin)
    PersistenceManager.blockchain.saveBlock(block)
    block
  }

}
