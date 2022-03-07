package it.unifi.nave.uniblock
package helper.crypto

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}

object AESHelper {
  private val AES_KEY_SIZE_BYTE = 16
  private val GCM_TAG_SIZE_BIT = 128
  private val IV_GCM_SIZE_BYTE = 12
  private val SYMMETRIC_CIPHER = "AES"
  private val AES_SUITE = "AES/GCM/NoPadding"

  def encrypt(secret: Array[Byte], plain: Either[Array[Byte], String], derive: Boolean = true): String = {
    val key = if (derive) deriveKey(secret) else secret
    val iv = RandomHelper.generateRandom(IV_GCM_SIZE_BYTE)
    val cipher = Cipher.getInstance(AES_SUITE)
    val parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv)
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec)
    val cipherText = cipher.doFinal(plain match {
      case Left(value) => value
      case Right(value) => value.getBytes(StandardCharsets.UTF_8)
    })
    val result = ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array
    Base64.getEncoder.encodeToString(result)
  }

  def decrypt(secret: Array[Byte], encrypted: Either[Array[Byte], String], derive: Boolean = true): String = {
    val key = if (derive) deriveKey(secret) else secret
    val cipher = Cipher.getInstance(AES_SUITE)
    val bytes = encrypted match {
      case Left(value) => value
      case Right(value) => Base64.getDecoder.decode(value)
    }
    val parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, bytes, 0, IV_GCM_SIZE_BYTE)
    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec)
    val result = cipher.doFinal(bytes, IV_GCM_SIZE_BYTE, bytes.length - IV_GCM_SIZE_BYTE)
    new String(result, StandardCharsets.UTF_8)
  }

  private def deriveKey(source: Array[Byte]): Array[Byte] = HashHelper.hashRaw(Left(source)).take(AES_KEY_SIZE_BYTE)

  def randomKey(): Array[Byte] = RandomHelper.generateRandom(AES_KEY_SIZE_BYTE)

}
