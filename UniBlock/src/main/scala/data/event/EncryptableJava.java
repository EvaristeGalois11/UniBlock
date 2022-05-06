package it.unifi.nave.uniblock.data.event;

import java.io.Serializable;
import java.time.LocalDate;

public interface EncryptableJava extends Serializable {

    EventType getType();

    enum EventType {
        GENESIS, CERTIFICATE, EXAM_PUBLISHING, EXAM_BOOKING, EXAM_RESULT, EXAM_CONFIRM
    }

    record ExamPublishing(String professor, String codeExam, LocalDate date) implements EncryptableJava {
        @Override
        public EventType getType() {
            return EventType.EXAM_PUBLISHING;
        }
    }

    record ExamBooking(String student, String codeExam, LocalDate date) implements EncryptableJava {
        @Override
        public EventType getType() {
            return EventType.EXAM_BOOKING;
        }
    }

    record ExamResult(String professor, String student, String codeExam, LocalDate date,
                      int result) implements EncryptableJava {
        @Override
        public EventType getType() {
            return EventType.EXAM_RESULT;
        }
    }

    record ExamConfirm(String student, String codeExam, LocalDate date, boolean confirm) implements EncryptableJava {
        @Override
        public EventType getType() {
            return EventType.EXAM_CONFIRM;
        }
    }
}
