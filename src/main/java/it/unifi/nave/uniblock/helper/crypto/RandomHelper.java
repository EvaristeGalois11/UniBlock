package it.unifi.nave.uniblock.helper.crypto;

import java.security.SecureRandom;

public class RandomHelper {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] generateRandom(int length) {
        var random = new byte[length];
        SECURE_RANDOM.nextBytes(random);
        return random;
    }
}
