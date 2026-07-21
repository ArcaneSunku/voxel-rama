package atomixsoft.dev.world;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public record WorldSeed(long value, String originalInput, WorldSeedSource source) {

    public WorldSeed {
        if (originalInput == null)
            throw new IllegalArgumentException("Original seed input cannot be null.");

        if (source == null)
            throw new IllegalArgumentException("World seed source cannot be null.");
    }

    public static WorldSeed random() {
        return new WorldSeed(ThreadLocalRandom.current().nextLong(), "", WorldSeedSource.RANDOM);
    }

    public static WorldSeed fromInput(String input) {
        if (input == null || input.isBlank())
            return random();

        String normalizedInput = input.trim();

        try {
            long numericSeed = Long.parseLong(normalizedInput);
            return new WorldSeed(numericSeed, normalizedInput, WorldSeedSource.NUMERIC);
        } catch (NumberFormatException ignored) {
            long textSeed = hashTextSeed(normalizedInput);
            return new WorldSeed(textSeed, normalizedInput, WorldSeedSource.TEXT);
        }
    }

    private static long hashTextSeed(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return ByteBuffer.wrap(hash).getLong();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }

    public boolean wasRandomlyGenerated() {
        return source == WorldSeedSource.RANDOM;
    }

    public boolean cameFromText() {
        return source == WorldSeedSource.TEXT;
    }
}
