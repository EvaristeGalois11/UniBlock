package it.unifi.nave.uniblock.data.event;

import java.io.Serializable;
import java.time.LocalDate;

public interface Encryptable extends Serializable {

    EventType getType();

    enum EventType {
        EXAM_PUBLISHING, EXAM_BOOKING, EXAM_RESULT, EXAM_CONFIRM
    }

    record ExamPublishing(String professor, String codeExam, LocalDate date) implements Encryptable {
        @Override
        public EventType getType() {
            return EventType.EXAM_PUBLISHING;
        }
    }

    record ExamBooking(String student, String codeExam, LocalDate date) implements Encryptable {
        @Override
        public EventType getType() {
            return EventType.EXAM_BOOKING;
        }
    }

    record ExamResult(String professor, String student, String codeExam, LocalDate date,
                      int result) implements Encryptable {
        @Override
        public EventType getType() {
            return EventType.EXAM_RESULT;
        }
    }

    record ExamConfirm(String student, String codeExam, LocalDate date, boolean confirm) implements Encryptable {
        @Override
        public EventType getType() {
            return EventType.EXAM_CONFIRM;
        }
    }
}
