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
package it.unifi.nave.uniblock.data.event;

import java.io.Serializable;
import java.time.LocalDate;

public interface Encryptable extends Serializable {

  EventType getType();

  enum EventType {
    EXAM_PUBLISHING,
    EXAM_BOOKING,
    EXAM_RESULT,
    EXAM_CONFIRM
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

  record ExamResult(String professor, String student, String codeExam, LocalDate date, int result)
      implements Encryptable {
    @Override
    public EventType getType() {
      return EventType.EXAM_RESULT;
    }
  }

  record ExamConfirm(String student, String codeExam, LocalDate date, boolean confirm)
      implements Encryptable {
    @Override
    public EventType getType() {
      return EventType.EXAM_CONFIRM;
    }
  }
}
