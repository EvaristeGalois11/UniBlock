/*
 *Copyright (C) 2023 Claudio Nave
 *
 *This file is part of UniBlock.
 *
 *UniBlock is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *UniBlock is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with UniBlock. If not, see <https://www.gnu.org/licenses/>.
 */
package it.unifi.nave.uniblock.service.demo;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.data.event.Encryptable;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.persistence.Blockchain;
import it.unifi.nave.uniblock.persistence.KeyManager;
import it.unifi.nave.uniblock.service.crypto.HashService;
import it.unifi.nave.uniblock.service.crypto.PKService;
import it.unifi.nave.uniblock.service.data.CertificateService;
import it.unifi.nave.uniblock.service.data.EncryptedEventService;
import it.unifi.nave.uniblock.service.data.MerkleTreeService;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;

public class DemoService {
  private final CertificateService certificateService;
  private final EncryptedEventService encryptedEventService;
  private final MerkleTreeService merkleTreeService;
  private final HashService hashService;
  private final PKService pkService;
  private final Blockchain blockchain;
  private final KeyManager keyManager;
  private final MinerService minerService;
  private final ToStringService toStringService;
  private final PrintService printService;

  private int difficulty;
  private boolean progress;

  @Inject
  public DemoService(
      CertificateService certificateService,
      EncryptedEventService encryptedEventService,
      MerkleTreeService merkleTreeService,
      HashService hashService,
      PKService pkService,
      Blockchain blockchain,
      KeyManager keyManager,
      MinerService minerService,
      ToStringService toStringService,
      PrintService printService) {
    this.certificateService = certificateService;
    this.encryptedEventService = encryptedEventService;
    this.merkleTreeService = merkleTreeService;
    this.hashService = hashService;
    this.pkService = pkService;
    this.blockchain = blockchain;
    this.keyManager = keyManager;
    this.minerService = minerService;
    this.toStringService = toStringService;
    this.printService = printService;
  }

  public void startDemo(int difficulty, boolean progress) {
    this.difficulty = difficulty;
    this.progress = progress;
    var genesis = initGenesis();
    var certificates = createUsers(hashService.hash(genesis.getBlockHeader()));
    var professor =
        certificates.getEvents().stream()
            .filter(Certificate.class::isInstance)
            .map(Certificate.class::cast)
            .filter(c -> c.certificateType() == Certificate.CertificateType.PROFESSOR)
            .map(Certificate::userId)
            .findAny()
            .orElseThrow();
    var students =
        certificates.getEvents().stream()
            .filter(Certificate.class::isInstance)
            .map(Certificate.class::cast)
            .filter(c -> c.certificateType() == Certificate.CertificateType.STUDENT)
            .map(Certificate::userId)
            .toList();
    var publishing =
        publishExam(professor, students, hashService.hash(certificates.getBlockHeader()));
    var booking = bookExam(professor, students, hashService.hash(publishing.getBlockHeader()));
    var result = publishResult(professor, students, hashService.hash(booking.getBlockHeader()));
    confirmExam(professor, students, hashService.hash(result.getBlockHeader()));
  }

  private Block initGenesis() {
    var genesisDhPk = pkService.generateDhKeyPair();
    var genesisSignPk = pkService.generateSignKeyPair();
    var genesisCertificate =
        certificateService.build(
            "",
            genesisSignPk.getPublic(),
            genesisDhPk.getPublic(),
            Certificate.CertificateType.GENESIS);
    registerPk(genesisSignPk.getPrivate(), genesisDhPk.getPrivate(), Certificate.GENESIS);
    return mineBlock(
        "GENESIS_BLOCK", "Generating genesis block", Collections.singletonList(genesisCertificate));
  }

  private Block createUsers(String previousHash) {
    var professorEvent = createUser("Francesco Tiezzi", Certificate.CertificateType.PROFESSOR);
    var studentEvent1 = createUser("Claudio Nave", Certificate.CertificateType.STUDENT);
    var studentEvent2 = createUser("Mario Rossi", Certificate.CertificateType.STUDENT);
    return mineBlock(
        previousHash,
        "Generating professor and students certificates",
        List.of(professorEvent, studentEvent1, studentEvent2));
  }

  private Certificate createUser(String name, Certificate.CertificateType certificateType) {
    var professorDhPk = pkService.generateDhKeyPair();
    var professorSignPk = pkService.generateSignKeyPair();
    var professorCertificate =
        certificateService.build(
            name, professorSignPk.getPublic(), professorDhPk.getPublic(), certificateType);
    registerPk(
        professorSignPk.getPrivate(), professorDhPk.getPrivate(), professorCertificate.userId());
    return professorCertificate;
  }

  private Block publishExam(String professor, List<String> students, String previousHash) {
    var event =
        new Encryptable.ExamPublishing(
            professor, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29));
    var eventContainer = encryptedEventService.build(event, professor, students);
    return mineBlock(previousHash, "Publishing exam", Collections.singletonList(eventContainer));
  }

  private Block bookExam(String professor, List<String> students, String previousHash) {
    return mineBlock(
        previousHash,
        "Booking exams",
        students.stream()
            .map(
                s ->
                    new Encryptable.ExamBooking(
                        s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29)))
            .map(
                e ->
                    encryptedEventService.build(
                        e, e.student(), Collections.singletonList(professor)))
            .toList());
  }

  private Block publishResult(String professor, List<String> students, String previousHash) {
    return mineBlock(
        previousHash,
        "Publishing results",
        students.stream()
            .map(
                s ->
                    new Encryptable.ExamResult(
                        professor,
                        s,
                        "PROGRAMMAZIONE",
                        LocalDate.of(2017, Month.JUNE, 29),
                        ThreadLocalRandom.current().nextInt(18, 31)))
            .map(
                e ->
                    encryptedEventService.build(
                        e, professor, Collections.singletonList(e.student())))
            .toList());
  }

  private void confirmExam(String professor, List<String> students, String previousHash) {
    mineBlock(
        previousHash,
        "Accepting exams",
        students.stream()
            .map(
                s ->
                    new Encryptable.ExamConfirm(
                        s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), true))
            .map(
                e ->
                    encryptedEventService.build(
                        e, e.student(), Collections.singletonList(professor)))
            .toList());
  }

  private void registerPk(PrivateKey signPk, PrivateKey dhPk, String id) {
    keyManager.saveSignPk(id, signPk);
    keyManager.saveDhPk(id, dhPk);
  }

  private Block mineBlock(String previousHash, String message, List<? extends Event> events) {
    var block =
        new Block(previousHash, difficulty, events, merkleTreeService.calculateRootHash(events));
    printService.print(message);
    var start = Instant.now();
    minerService.mine(block, progress);
    var end = Instant.now();
    var duration = Duration.between(start, end);
    printService.println(
        "\nBlock mined in "
            + duration.toMinutes()
            + "m "
            + duration.toSecondsPart()
            + "s\n"
            + toStringService.blockToString(block)
            + "\n");
    blockchain.saveBlock(block);
    return block;
  }
}
