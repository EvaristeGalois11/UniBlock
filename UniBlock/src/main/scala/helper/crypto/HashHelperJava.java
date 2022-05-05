package it.unifi.nave.uniblock.helper.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HashHelperJava {
    private static final String HASH_TYPE = "SHA3-256";

    public static String hash(String toHash) {
        return HexFormat.of().formatHex(hashRaw(toHash));
    }

    public static String hash(byte[] toHash) {
        return HexFormat.of().formatHex(hashRaw(toHash));
    }

    public static byte[] hashRaw(String toHash) {
        return hashRaw(toHash.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hashRaw(byte[] toHash) {
        try {
            return MessageDigest.getInstance(HASH_TYPE).digest(toHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
