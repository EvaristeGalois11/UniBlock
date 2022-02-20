package it.unifi.nave.uniblock
package crypto

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security._
import java.util.Base64
import javax.crypto.KeyAgreement

object PKHelper {
  private val PUBLIC_KEY_DH = "X25519"
  private val PUBLIC_KEY_AGREEMENT = "XDH"
  private val PUBLIC_KEY_SIGN = "Ed25519"
  private val SIGN_SIZE_BYTE = 64

  def generateDhKeyPair: KeyPair = generateKeyPair(PUBLIC_KEY_DH)

  def generateSignKeyPair: KeyPair = generateKeyPair(PUBLIC_KEY_SIGN)

  private def generateKeyPair(`type`: String) = KeyPairGenerator.getInstance(`type`).generateKeyPair

  def encryptEncoded(pk: PrivateKey, pbk: PublicKey, string: String): String = encryptEncoded(pk, pbk, string.getBytes(StandardCharsets.UTF_8))

  def encryptEncoded(pk: PrivateKey, pbk: PublicKey, bytes: Array[Byte]): String = Base64.getEncoder.encodeToString(encrypt(pk, pbk, bytes))

  def encrypt(pk: PrivateKey, pbk: PublicKey, string: String): Array[Byte] = encrypt(pk, pbk, string.getBytes(StandardCharsets.UTF_8))

  def encrypt(pk: PrivateKey, pbk: PublicKey, string: Array[Byte]): Array[Byte] = {
    val secret = generateSecret(pk, pbk)
    AESHelper.encrypt(secret, string, true)
  }

  def decryptEncoded(pk: PrivateKey, pbk: PublicKey, string: String): String = {
    val secret = generateSecret(pk, pbk)
    AESHelper.decryptEncoded(secret, string, true)
  }

  private def generateSecret(privateKey: PrivateKey, publicKey: PublicKey): Array[Byte] = {
    val agreement = KeyAgreement.getInstance(PUBLIC_KEY_AGREEMENT)
    agreement.init(privateKey)
    agreement.doPhase(publicKey, true)
    agreement.generateSecret
  }

  def sign(bytes: Array[Byte], pk: PrivateKey): Array[Byte] = {
    val signature = Signature.getInstance(PUBLIC_KEY_SIGN)
    signature.initSign(pk)
    signature.update(bytes)
    val sign = signature.sign
    ByteBuffer.allocate(sign.length + bytes.length).put(sign).put(bytes).array
  }

  def verify(bytes: Array[Byte], pbk: PublicKey): Boolean = {
    val sig = Signature.getInstance(PUBLIC_KEY_SIGN)
    sig.initVerify(pbk)
    sig.update(bytes, SIGN_SIZE_BYTE, bytes.length - SIGN_SIZE_BYTE)
    sig.verify(bytes, 0, SIGN_SIZE_BYTE)
  }

}
