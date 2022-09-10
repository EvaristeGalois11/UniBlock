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
