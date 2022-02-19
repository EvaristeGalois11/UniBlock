package it.unifi.nave.data.event;

import java.time.Instant;

public record ExamResult(String professor, String student, String codeExam,
                         Instant instant, int result) implements Event {
}
