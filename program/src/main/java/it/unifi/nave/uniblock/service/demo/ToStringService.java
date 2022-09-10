package it.unifi.nave.uniblock.service.demo;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import it.unifi.nave.uniblock.data.block.Block;
import it.unifi.nave.uniblock.data.block.BlockHeader;
import it.unifi.nave.uniblock.data.event.Certificate;
import it.unifi.nave.uniblock.data.event.EncryptedEvent;
import it.unifi.nave.uniblock.data.event.Event;
import it.unifi.nave.uniblock.service.crypto.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class ToStringService {
  private static final int LINE_LENGTH = 100;
  private static final String PADDING = "-";
  private static final int MARGIN = 5;
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

  private String formatCenter(String string) {
    var normalized = (string.length() % 2 == 0) ? string : string + PADDING;
    var toFill = LINE_LENGTH - string.length();
    return PADDING.repeat(toFill / 2) + normalized + PADDING.repeat(toFill / 2);
  }

  private String formatTitle(String title) {
    return emptyLine() + "\n" + formatCenter(title.toUpperCase()) + "\n" + emptyLine();
  }

  private String formatLeft(Object object, String label) {
    return formatLeft(object.toString(), label);
  }

  private String formatLeft(String string, String label) {
    var margin = PADDING.repeat(MARGIN);
    var fieldName =
        margin + Strings.padEnd(" " + label + " ", FIELD_LENGTH, PADDING.charAt(0)) + " = ";
    var leftMargin = PADDING.repeat(fieldName.length() - 1) + " ";
    var realLine = LINE_LENGTH - fieldName.length() - MARGIN - 1;
    var rightMargin =
        " "
            + ((string.length() % realLine != 0)
                ? PADDING.repeat(realLine - string.length() % realLine)
                : "")
            + margin;
    return Splitter.fixedLength(realLine).splitToList(string).stream()
        .collect(Collectors.joining(" " + margin + "\n" + leftMargin, fieldName, rightMargin));
  }
}
