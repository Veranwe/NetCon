package src.pre.NetCon.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SymmetricCrypt {

    /**
     * The key generator for symmetric encryption.
     * @return a symmetric key.
     */
    public static SecretKey genSymKey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(256);

            return gen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates the initialization vector for the symmetric encryption.
     * @return The initialization vector.
     */
    public static byte[] genInitVector() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return iv;
    }

    /**
     * Encrypts the given data using the provided key and initialization vector.
     * @param data The data to be encrypted.
     * @param key The key which it's encrypted behind.
     * @param iv The initialization vector which it's encrypted behind.
     * @return The symmetrically encrypted data.
     */
    public static byte[] encrypt(byte[] data, SecretKey key, byte[] iv) {
        return crypt(data, key, iv, Cipher.ENCRYPT_MODE);
    }
    /**
     * Decrypts the given data using the provided key and initialization vector.
     * @param data The data to be decrypted.
     * @param key The key which it's encrypted behind.
     * @param iv The initialization vector which it's encrypted behind.
     * @return The symmetrically decrypted data.
     */
    public static byte[] decrypt(byte[] data, SecretKey key, byte[] iv) {
        return crypt(data, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * The encryption and decryption functionality.
     * @param data The data to be encrypted or decrypted.
     * @param key The key which it's encrypted behind.
     * @param iv The initialization vector which it's encrypted behind.
     * @param opMode The operation being done. Either ENCRYPT or DECRYPT.
     * @return Returns the encrypted or decrypted data.
     */
    private static byte[] crypt(byte[] data, SecretKey key, byte[] iv, int opMode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opMode, key, new IvParameterSpec(iv));

            return cipher.doFinal(data);

        } catch (NoSuchPaddingException
                 | NoSuchAlgorithmException
                 | IllegalBlockSizeException
                 | InvalidAlgorithmParameterException
                 | BadPaddingException
                 | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encode(SecretKey key) {
        return key.getEncoded();
    }
    public static SecretKey decode(byte[] data) {
        return new SecretKeySpec(data, "AES");
    }
}
