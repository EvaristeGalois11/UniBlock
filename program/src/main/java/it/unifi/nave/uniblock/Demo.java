package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.data.event.Encryptable;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.helper.CertificateHelper;
import it.unifi.nave.uniblock.helper.EncryptedEventHelper;
import it.unifi.nave.uniblock.helper.HashHelper;
import it.unifi.nave.uniblock.helper.PKHelper;
import it.unifi.nave.uniblock.helper.PersistenceHelper;
import it.unifi.nave.uniblock.service.MinerService;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Demo(int difficulty) {

    public void startDemo(){
        var genesis = initGenesis();
        var certificates = createUsers(HashHelper.hash(genesis.getBlockHeader()));
        var professor = certificates.getEvents().stream()
                .filter(Certificate.class::isInstance)
                .map(Certificate.class::cast)
                .filter(c -> c.certificateType() == Certificate.CertificateType.PROFESSOR)
                .map(Certificate::userId)
                .findAny().orElseThrow();
        var students = certificates.getEvents().stream()
                .filter(Certificate.class::isInstance)
                .map(Certificate.class::cast)
                .filter(c -> c.certificateType() == Certificate.CertificateType.STUDENT)
                .map(Certificate::userId)
                .toList();
        var publishing = publishExam(professor, students, HashHelper.hash(certificates.getBlockHeader()));
        var booking = bookExam(professor, students, HashHelper.hash(publishing.getBlockHeader()));
        var result = publishResult(professor, students, HashHelper.hash(booking.getBlockHeader()));
        confirmExam(professor, students, HashHelper.hash(result.getBlockHeader()));
        MinerService.terminate();
    }

    private Block initGenesis() {
        var genesisDhPk = PKHelper.generateDhKeyPair();
        var genesisSignPk = PKHelper.generateSignKeyPair();
        var genesisCertificate = CertificateHelper.build("", genesisSignPk.getPublic(), genesisDhPk.getPublic(), Certificate.CertificateType.GENESIS);
        registerPk(genesisSignPk.getPrivate(), genesisDhPk.getPrivate(), Certificate.GENESIS);
        return mineBlock("GENESIS_BLOCK", "Generating genesis block", genesisCertificate);
    }

    private Block createUsers(String previousHash) {
        var professorEvent = createUser("Lorenzo Bettini", Certificate.CertificateType.PROFESSOR);
        var studentEvent1 = createUser("Claudio Nave", Certificate.CertificateType.STUDENT);
        var studentEvent2 = createUser("Mario Rossi", Certificate.CertificateType.STUDENT);
        return mineBlock(previousHash, "Generating professor and students certificates", professorEvent, studentEvent1, studentEvent2);
    }

    private Certificate createUser(String name, Certificate.CertificateType certificateType) {
        var professorDhPk = PKHelper.generateDhKeyPair();
        var professorSignPk = PKHelper.generateSignKeyPair();
        var professorCertificate = CertificateHelper.build(name, professorSignPk.getPublic(), professorDhPk.getPublic(), certificateType);
        registerPk(professorSignPk.getPrivate(), professorDhPk.getPrivate(), professorCertificate.userId());
        return professorCertificate;
    }

    private Block publishExam(String professor, List<String> students, String previousHash) {
        var event = new Encryptable.ExamPublishing(professor, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29));
        var eventContainer = EncryptedEventHelper.build(event, professor, students);
        return mineBlock(previousHash, "Publishing exam", eventContainer);
    }

    private Block bookExam(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Booking exams", students.stream()
                .map(s -> new Encryptable.ExamBooking(s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29)))
                .map(e -> EncryptedEventHelper.build(e, e.student(), Collections.singletonList(professor))).toArray(Event[]::new));
    }

    private Block publishResult(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Publishing results", students.stream()
                .map(s -> new Encryptable.ExamResult(professor, s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), ThreadLocalRandom.current().nextInt(18, 31)))
                .map(e -> EncryptedEventHelper.build(e, professor, Collections.singletonList(e.student()))).toArray(Event[]::new));
    }

    private Block confirmExam(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Accepting exams", students.stream()
                .map(s -> new Encryptable.ExamConfirm(s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), true))
                .map(e -> EncryptedEventHelper.build(e, e.student(), Collections.singletonList(professor))).toArray(Event[]::new));
    }

    private void registerPk(PrivateKey signPk, PrivateKey dhPk, String id) {
        PersistenceHelper.getKeyManager().saveSignPk(id, signPk);
        PersistenceHelper.getKeyManager().saveDhPk(id, dhPk);
    }

    private Block mineBlock(String previousHash, String message, Event... events) {
        var block = new Block(previousHash, difficulty);
        block.addEvents(Arrays.asList(events));
        System.out.print(message);
        var start = Instant.now();
        MinerService.mine(block);
        var end = Instant.now();
        var duration = Duration.between(start, end);
        System.out.println("\nBlock mined in " + duration.toMinutes() + "m " + duration.toSecondsPart() + "s\n" + block + "\n");
        PersistenceHelper.getBlockchain().saveBlock(block);
        return block;
    }
}
