package it.unifi.nave.uniblock
package helper.crypto

import java.nio.charset.StandardCharsets
import java.security._
import java.util.Base64
import javax.crypto.KeyAgreement

object PKHelper {
  private val PUBLIC_KEY_DH = "X25519"
  private val PUBLIC_KEY_AGREEMENT = "XDH"
  private val PUBLIC_KEY_SIGN = "Ed25519"

  def generateDhKeyPair: KeyPair = generateKeyPair(PUBLIC_KEY_DH)

  def generateSignKeyPair: KeyPair = generateKeyPair(PUBLIC_KEY_SIGN)

  private def generateKeyPair(typeKp: String) = KeyPairGenerator.getInstance(typeKp).generateKeyPair

  def encrypt(pk: PrivateKey, pbk: PublicKey, plain: Either[Array[Byte], String]): String = {
    val secret = generateSecret(pk, pbk)
    AESHelper.encrypt(secret, plain)
  }

  def decrypt(pk: PrivateKey, pbk: PublicKey, encrypted: Either[Array[Byte], String]): String = {
    val secret = generateSecret(pk, pbk)
    AESHelper.decrypt(secret, encrypted)
  }

  private def generateSecret(privateKey: PrivateKey, publicKey: PublicKey): Array[Byte] = {
    val agreement = KeyAgreement.getInstance(PUBLIC_KEY_AGREEMENT)
    agreement.init(privateKey)
    agreement.doPhase(publicKey, true)
    agreement.generateSecret
  }

  def sign(toSign: Either[Array[Byte], String], pk: PrivateKey): String = {
    val signature = Signature.getInstance(PUBLIC_KEY_SIGN)
    signature.initSign(pk)
    signature.update(toSign match {
      case Left(value) => value
      case Right(value) => value.getBytes(StandardCharsets.UTF_8)
    })
    Base64.getEncoder.encodeToString(signature.sign)
  }

  def verify(toVerify: Either[Array[Byte], String], sign: String, pbk: PublicKey): Boolean = {
    val sig = Signature.getInstance(PUBLIC_KEY_SIGN)
    sig.initVerify(pbk)
    sig.update(toVerify match {
      case Left(value) => value
      case Right(value) => value.getBytes(StandardCharsets.UTF_8)
    })
    sig.verify(Base64.getDecoder.decode(sign))
  }

}
