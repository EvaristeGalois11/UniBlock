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
package it.unifi.nave.uniblock.service.data;

import com.google.common.collect.Lists;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.service.crypto.HashService;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MerkleTreeService {
  private final HashService hashService;

  @Inject
  public MerkleTreeService(HashService hashService) {
    this.hashService = hashService;
  }

  public String calculateRootHash(List<? extends Event> events) {
    return getRootHash(events.stream().map(hashService::hash).toList());
  }

  private String getRootHash(List<String> hashes) {
    if (hashes.size() == 1) {
      return hashes.getFirst();
    } else {
      return getRootHash(Lists.partition(hashes, 2).stream().map(this::reduceHash).toList());
    }
  }

  private String reduceHash(List<String> hashes) {
    var first = hashes.get(0);
    var second = hashes.size() == 2 ? hashes.get(1) : first;
    return hashService.hash(first + second);
  }
}
