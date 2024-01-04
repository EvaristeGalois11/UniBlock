/*
 *Copyright (C) 2024 Claudio Nave
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
package it.unifi.nave.uniblock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unifi.nave.uniblock.test.DaggerTestFactory;
import it.unifi.nave.uniblock.test.TestFactory;
import it.unifi.nave.uniblock.util.InstantUtil;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniBlockIT {
  private static final int TEST_DIFFICULTY = 3;
  private static final String OUTPUT_FILE = "/output.txt";
  private static final Instant FIXED_INSTANT = Instant.parse("2007-12-03T10:15:30Z");

  private TestFactory testFactory;

  @BeforeEach
  void setUp() {
    testFactory = DaggerTestFactory.create();
    InstantUtil.setClock(Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC));
  }

  @Test
  void test() throws IOException {
    var demo = testFactory.getDemo();
    demo.startDemo(TEST_DIFFICULTY, false);
    assertOutput();
  }

  private void assertOutput() throws IOException {
    var expected = loadFile();
    var print = testFactory.getPrint();
    assertEquals(expected, print.getOutput());
  }

  private String loadFile() throws IOException {
    try (var input = getClass().getResourceAsStream(OUTPUT_FILE)) {
      return new String(input.readAllBytes());
    }
  }
}
