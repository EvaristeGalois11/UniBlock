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
package it.unifi.nave.uniblock.service.demo;

import com.google.common.base.Splitter;
import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.block.BlockHeader;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.data.event.EncryptedEvent;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.service.crypto.HashService;
import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class ToStringService {
  private static final int LINE_LENGTH = 90;
  private static final String PADDING = "-";
  private static final int MARGIN_LENGTH = 2;
  private static final int FIELD_LENGTH = 20;

  private final HashService hashService;

  @Inject
  public ToStringService(HashService hashService) {
    this.hashService = hashService;
  }

  public String blockToString(Block block) {
    return formatTitle("Block Header")
        + "\n"
        + blockHeaderToString(block.getBlockHeader())
        + "\n"
        + formatTitle("Events")
        + "\n"
        + eventsToString(block.getEvents())
        + "\n"
        + emptyLine();
  }

  public String blockHeaderToString(BlockHeader blockHeader) {
    return formatLeft(hashService.hash(blockHeader), "hash")
        + "\n"
        + formatLeft(blockHeader.getPreviousHash(), "previousHash")
        + "\n"
        + formatLeft(blockHeader.getDifficulty(), "difficulty")
        + "\n"
        + formatLeft(blockHeader.getRootHash(), "rootHash")
        + "\n"
        + formatLeft(timestampToString(blockHeader.getTimestamp()), "timestamp")
        + "\n"
        + formatLeft(blockHeader.getNonce(), "nonce");
  }

  private String timestampToString(Instant timestamp) {
    return timestamp
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
  }

  private String eventsToString(Collection<Event> events) {
    return events.stream()
        .map(this::eventToString)
        .collect(Collectors.joining("\n" + emptyLine() + "\n"));
  }

  public String eventToString(Event event) {
    if (event instanceof Certificate certificate) {
      return certificateToString(certificate);
    } else if (event instanceof EncryptedEvent encryptedEvent) {
      return encryptedEventToString(encryptedEvent);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private String certificateToString(Certificate certificate) {
    return formatLeft(hashService.hash(certificate), "hash")
        + "\n"
        + formatLeft(certificate.userId(), "userId")
        + "\n"
        + formatLeft(certificate.name(), "name")
        + "\n"
        + formatLeft(certificate.certificateType(), "certificateType")
        + "\n"
        + keyToString(certificate.signPbk(), "sign")
        + "\n"
        + keyToString(certificate.dhPbk(), "dh")
        + "\n"
        + formatLeft(certificate.sign(), "signature");
  }

  private String keyToString(PublicKey pbk, String label) {
    return formatLeft(Base64.getEncoder().encodeToString(pbk.getEncoded()), label + " public key");
  }

  private String encryptedEventToString(EncryptedEvent encryptedEvent) {
    return formatLeft(hashService.hash(encryptedEvent), "hash")
        + "\n"
        + formatLeft(encryptedEvent.getAuthor(), "author")
        + "\n"
        + formatLeft(encryptedEvent.getEventType(), "eventType")
        + "\n"
        + mapOfKeysToString(encryptedEvent.getMapOfKeys())
        + "\n"
        + formatLeft(encryptedEvent.getPayload(), "encrypted event")
        + "\n"
        + formatLeft(encryptedEvent.getSign(), "signature");
  }

  private String mapOfKeysToString(Map<String, String> mapOfKeys) {
    return mapOfKeys.entrySet().stream()
        .map(e -> formatLeft(e.getKey(), "receiver") + "\n" + formatLeft(e.getValue(), "key"))
        .collect(Collectors.joining("\n"));
  }

  private String emptyLine() {
    return PADDING.repeat(LINE_LENGTH);
  }

  private String formatTitle(String title) {
    return emptyLine()
        + "\n"
        + StringUtils.center(title.toUpperCase(), LINE_LENGTH, PADDING)
        + "\n"
        + emptyLine();
  }

  private String formatLeft(Object object, String label) {
    return formatLeft(object.toString(), label);
  }

  private String formatLeft(String string, String label) {
    var margin = PADDING.repeat(MARGIN_LENGTH);
    var fieldName = StringUtils.rightPad(margin + " " + label + " ", FIELD_LENGTH, PADDING) + " =";
    var contentLine = LINE_LENGTH - fieldName.length() - MARGIN_LENGTH;
    return Splitter.fixedLength(contentLine - 2).splitToList(string).stream()
        .map(s -> " " + s + " ")
        .map(s -> StringUtils.rightPad(s, contentLine + MARGIN_LENGTH, PADDING))
        .collect(Collectors.joining("\n" + PADDING.repeat(fieldName.length()), fieldName, ""));
  }
}
