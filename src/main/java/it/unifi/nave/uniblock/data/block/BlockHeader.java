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
package it.unifi.nave.uniblock.data.block;

import it.unifi.nave.uniblock.util.InstantUtil;
import java.io.Serializable;
import java.time.Instant;

public class BlockHeader implements Serializable, Cloneable {
  private final String previousHash;
  private final int difficulty;
  private final String rootHash;
  private final Instant timestamp = InstantUtil.now();

  private int nonce = 0;

  public BlockHeader(String previousHash, int difficulty, String rootHash) {
    this.previousHash = previousHash;
    this.difficulty = difficulty;
    this.rootHash = rootHash;
  }

  @Override
  public BlockHeader clone() {
    try {
      return (BlockHeader) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public int getDifficulty() {
    return difficulty;
  }

  public String getRootHash() {
    return rootHash;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public int getNonce() {
    return nonce;
  }

  public void setNonce(int nonce) {
    this.nonce = nonce;
  }
}
