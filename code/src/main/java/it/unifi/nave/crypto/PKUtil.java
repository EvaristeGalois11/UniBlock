package it.unifi.nave.crypto;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class PKUtil {

  public void test() {
    try {
      var test = "uno di noi";
      System.out.println("The string that will be exchanged: " + test);
      // ECC KEYS GENERATION
      var kpg = KeyPairGenerator.getInstance("X25519");
      var kp1 = kpg.generateKeyPair();
      var kp2 = kpg.generateKeyPair();

      // ENCRYPTION
      var secret1 = generateSecret(kp1.getPrivate(), kp2.getPublic());
      var encrypt = encrypt(secret1, test);
      System.out.println("String encrypted by the first user: " + encrypt);

      // DECRYPTION
      var secret2 = generateSecret(kp2.getPrivate(), kp1.getPublic());
      var decrypt = decrypt(secret2, Base64.getDecoder().decode(encrypt));
      System.out.println("String decrypted by the second user: " + decrypt);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private byte[] generateSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
    var agreement1 = KeyAgreement.getInstance("XDH");
    agreement1.init(privateKey);
    agreement1.doPhase(publicKey, true);
    return agreement1.generateSecret();
  }

  private String encrypt(byte[] secret, String string) throws Exception {
    var key = Arrays.copyOf(CryptoFactory.newHashUtil().hashRaw(secret), 16);
    var iv = CryptoFactory.generateRandom(12);
    var cipher = Cipher.getInstance("AES/GCM/NoPadding");
    var parameterSpec = new GCMParameterSpec(128, iv);
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
    var cipherText = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
    var cipherMessage =
        ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
    Arrays.fill(secret, (byte) 0);
    Arrays.fill(key, (byte) 0);
    Arrays.fill(iv, (byte) 0);
    return Base64.getEncoder().encodeToString(cipherMessage);
  }

  private String decrypt(byte[] secret, byte[] encoded) throws Exception {
    var key = Arrays.copyOf(CryptoFactory.newHashUtil().hashRaw(secret), 16);
    var cipher = Cipher.getInstance("AES/GCM/NoPadding");
    var parameterSpec = new GCMParameterSpec(128, encoded, 0, 12);
    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
    var plainText = cipher.doFinal(encoded, 12, encoded.length - 12);
    Arrays.fill(secret, (byte) 0);
    Arrays.fill(key, (byte) 0);
    return new String(plainText, StandardCharsets.UTF_8);
  }
}
