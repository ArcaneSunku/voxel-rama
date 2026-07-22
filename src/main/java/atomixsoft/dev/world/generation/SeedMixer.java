package atomixsoft.dev.world.generation;

public final class SeedMixer {

    private SeedMixer() {}

    public static long mix(long worldSeed, long salt) {
        long value = worldSeed + salt;

        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;

        return value ^ (value >>> 31);
    }

    public static int mixToInt(long worldSeed, long salt) {
        long mixed = mix(worldSeed, salt);
        return Long.hashCode(mixed);
    }

}