package com.example.privatechat.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT = "PrivateChatSecureSalt2024"
    private const val ITERATION_COUNT = 1000
    private const val KEY_LENGTH = 256

    private fun deriveKey(secretKey: String): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(secretKey.toCharArray(), SALT.toByteArray(), ITERATION_COUNT, KEY_LENGTH)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    /**
     * Encrypts plaintext string using AES-GCM
     */
    fun encrypt(plainText: String, secretKey: String = "PrivateRoomKey"): String {
        return try {
            val key = deriveKey(secretKey)
            val iv = ByteArray(IV_LENGTH_BYTE)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, spec)

            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val combined = iv + cipherText
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    /**
     * Decrypts ciphertext string using AES-GCM
     */
    fun decrypt(cipherTextBase64: String, secretKey: String = "PrivateRoomKey"): String {
        return try {
            val combined = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            if (combined.size < IV_LENGTH_BYTE) return cipherTextBase64

            val iv = combined.copyOfRange(0, IV_LENGTH_BYTE)
            val cipherText = combined.copyOfRange(IV_LENGTH_BYTE, combined.size)

            val key = deriveKey(secretKey)
            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainTextBytes = cipher.doFinal(cipherText)
            String(plainTextBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            cipherTextBase64
        }
    }

    /**
     * Hashes app PIN with SHA-256
     */
    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
