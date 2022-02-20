package it.unifi.nave.uniblock
package crypto

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}

object AESHelper {
  private val AES_KEY_SIZE_BYTE = 16
  private val GCM_TAG_SIZE_BIT = 128
  private val IV_GCM_SIZE_BYTE = 12
  private val SYMMETRIC_CIPHER = "AES"
  private val AES_SUITE = "AES/GCM/NoPadding"

  def encryptEncoded(secret: Array[Byte], string: String, derive: Boolean): String = encryptEncoded(secret, string.getBytes(StandardCharsets.UTF_8), derive)

  def encryptEncoded(secret: Array[Byte], bytes: Array[Byte], derive: Boolean): String = Base64.getEncoder.encodeToString(encrypt(secret, bytes, derive))

  def encrypt(secret: Array[Byte], string: String, derive: Boolean): Array[Byte] = encrypt(secret, string.getBytes(StandardCharsets.UTF_8), derive)

  def encrypt(secret: Array[Byte], bytes: Array[Byte], derive: Boolean): Array[Byte] = {
    val key = if (derive) deriveKey(secret) else secret
    val iv = RandomHelper.generateRandom(IV_GCM_SIZE_BYTE)
    val cipher = Cipher.getInstance(AES_SUITE)
    val parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, iv)
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec)
    val cipherText = cipher.doFinal(bytes)
    ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array
  }

  def decryptEncoded(secret: Array[Byte], string: String, derive: Boolean): String = decryptEncoded(secret, Base64.getDecoder.decode(string), derive)

  def decryptEncoded(secret: Array[Byte], bytes: Array[Byte], derive: Boolean) = new String(decrypt(secret, bytes, derive), StandardCharsets.UTF_8)

  def decrypt(secret: Array[Byte], string: String, derive: Boolean): Array[Byte] = decrypt(secret, Base64.getDecoder.decode(string), derive)

  def decrypt(secret: Array[Byte], bytes: Array[Byte], derive: Boolean): Array[Byte] = {
    val key = if (derive) deriveKey(secret)
    else secret
    val cipher = Cipher.getInstance(AES_SUITE)
    val parameterSpec = new GCMParameterSpec(GCM_TAG_SIZE_BIT, bytes, 0, IV_GCM_SIZE_BYTE)
    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, SYMMETRIC_CIPHER), parameterSpec)
    cipher.doFinal(bytes, IV_GCM_SIZE_BYTE, bytes.length - IV_GCM_SIZE_BYTE)
  }

  private def deriveKey(source: Array[Byte]): Array[Byte] = util.Arrays.copyOf(HashHelper.hashRaw(source), AES_KEY_SIZE_BYTE)

}
