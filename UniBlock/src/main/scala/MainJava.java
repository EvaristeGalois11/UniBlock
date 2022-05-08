package it.unifi.nave.uniblock;

import it.unifi.nave.uniblock.data.block.BlockJava;
import it.unifi.nave.uniblock.data.event.CertificateJava;
import it.unifi.nave.uniblock.data.event.EncryptableJava;
import it.unifi.nave.uniblock.data.event.EncryptedEventJava;
import it.unifi.nave.uniblock.data.event.EventJava;
import it.unifi.nave.uniblock.helper.crypto.HashHelperJava;
import it.unifi.nave.uniblock.helper.crypto.PKHelperJava;
import it.unifi.nave.uniblock.persistence.PersistenceManagerJava;
import it.unifi.nave.uniblock.service.MinerServiceJava;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainJava {
    private static int difficulty;

    public static void main(String[] args) {
        difficulty = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        var genesis = initGenesis();
        var certificates = createUsers(HashHelperJava.hash(genesis.getBlockHeader()));
        var professor = certificates.getEvents().stream()
                .filter(CertificateJava.class::isInstance)
                .map(CertificateJava.class::cast)
                .filter(c -> c.certificateType() == CertificateJava.CertificateType.PROFESSOR)
                .map(CertificateJava::userId)
                .findAny().orElseThrow();
        var students = certificates.getEvents().stream()
                .filter(CertificateJava.class::isInstance)
                .map(CertificateJava.class::cast)
                .filter(c -> c.certificateType() == CertificateJava.CertificateType.STUDENT)
                .map(CertificateJava::userId)
                .toList();
        var publishing = publishExam(professor, students, HashHelperJava.hash(certificates.getBlockHeader()));
        var booking = bookExam(professor, students, HashHelperJava.hash(publishing.getBlockHeader()));
        var result = publishResult(professor, students, HashHelperJava.hash(booking.getBlockHeader()));
        confirmExam(professor, students, HashHelperJava.hash(result.getBlockHeader()));
        MinerServiceJava.terminate();
    }

    private static BlockJava initGenesis() {
        var genesisDhPk = PKHelperJava.generateDhKeyPair();
        var genesisSignPk = PKHelperJava.generateSignKeyPair();
        var genesisCertificate = CertificateJava.build("", genesisSignPk.getPublic(), genesisDhPk.getPublic(), CertificateJava.CertificateType.GENESIS);
        registerPk(genesisSignPk.getPrivate(), genesisDhPk.getPrivate(), CertificateJava.GENESIS);
        return mineBlock("GENESIS_BLOCK", "Generating genesis block", genesisCertificate);
    }

    private static BlockJava createUsers(String previousHash) {
        var professorEvent = createUser("Lorenzo Bettini", CertificateJava.CertificateType.PROFESSOR);
        var studentEvent1 = createUser("Claudio Nave", CertificateJava.CertificateType.STUDENT);
        var studentEvent2 = createUser("Mario Rossi", CertificateJava.CertificateType.STUDENT);
        return mineBlock(previousHash, "Generating professor and students certificate", professorEvent, studentEvent1, studentEvent2);
    }

    private static CertificateJava createUser(String name, CertificateJava.CertificateType certificateType) {
        var professorDhPk = PKHelperJava.generateDhKeyPair();
        var professorSignPk = PKHelperJava.generateSignKeyPair();
        var professorCertificate = CertificateJava.build(name, professorSignPk.getPublic(), professorDhPk.getPublic(), certificateType);
        registerPk(professorSignPk.getPrivate(), professorDhPk.getPrivate(), professorCertificate.userId());
        return professorCertificate;
    }

    private static BlockJava publishExam(String professor, List<String> students, String previousHash) {
        var event = new EncryptableJava.ExamPublishing(professor, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29));
        var eventContainer = EncryptedEventJava.build(event, professor, students);
        return mineBlock(previousHash, "Publishing exam", eventContainer);
    }

    private static BlockJava bookExam(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Booking exams", students.stream()
                .map(s -> new EncryptableJava.ExamBooking(s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29)))
                .map(e -> EncryptedEventJava.build(e, e.student(), Collections.singletonList(professor))).toArray(EventJava[]::new));
    }

    private static BlockJava publishResult(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Publishing results", students.stream()
                .map(s -> new EncryptableJava.ExamResult(professor, s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), 0))
                .map(e -> EncryptedEventJava.build(e, professor, Collections.singletonList(e.student()))).toArray(EventJava[]::new));
    }

    private static BlockJava confirmExam(String professor, List<String> students, String previousHash) {
        return mineBlock(previousHash, "Booking exams", students.stream()
                .map(s -> new EncryptableJava.ExamConfirm(s, "PROGRAMMAZIONE", LocalDate.of(2017, Month.JUNE, 29), true))
                .map(e -> EncryptedEventJava.build(e, e.student(), Collections.singletonList(professor))).toArray(EventJava[]::new));
    }

    private static void registerPk(PrivateKey signPk, PrivateKey dhPk, String id) {
        PersistenceManagerJava.getKeyManager().saveSignPk(id, signPk);
        PersistenceManagerJava.getKeyManager().saveDhPk(id, dhPk);
    }

    private static BlockJava mineBlock(String previousHash, String message, EventJava... events) {
        var block = new BlockJava(previousHash, difficulty);
        block.addEvents(Arrays.asList(events));
        System.out.print(message);
        var start = Instant.now();
        MinerServiceJava.mine(block);
        var end = Instant.now();
        var duration = Duration.between(start, end);
        System.out.println("\nBlock mined in " + duration.toMinutes() + "m " + duration.toSecondsPart() + "s\n" + block + "\n");
        PersistenceManagerJava.getBlockchain().saveBlock(block);
        return block;
    }

}
