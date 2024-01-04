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
package it.unifi.nave.uniblock.service.crypto;

import java.security.SecureRandom;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RandomService {
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @Inject
  public RandomService() {}

  public byte[] generateRandom(int length) {
    var random = new byte[length];
    SECURE_RANDOM.nextBytes(random);
    return random;
  }
}
