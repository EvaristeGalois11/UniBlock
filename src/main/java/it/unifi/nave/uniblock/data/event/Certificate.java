/*
 *Copyright (C) 2022-2024 Claudio Nave
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

import java.security.PublicKey;

public record Certificate(
    String userId,
    String name,
    CertificateType certificateType,
    PublicKey signPbk,
    PublicKey dhPbk,
    String sign)
    implements Event {
  public static final String GENESIS = "GENESIS";

  public enum CertificateType {
    PROFESSOR,
    STUDENT,
    GENESIS
  }
}
