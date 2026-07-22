package atomixsoft.dev.noise;

import atomixsoft.dev.world.generation.SeedMixer;

public final class NoiseSamplers {

    private static final long TEMPERATURE_SEED_SALT = 0x5A17E4D39B28C601L;
    private static final long HUMIDITY_SEED_SALT = 0x3C91B72F68DA405EL;
    private static final long CONTINENTALNESS_SEED_SALT = 0x74E25CA10D9F83B6L;

    private NoiseSamplers() {}

    public static NoiseSampler2D createTemperature(long worldSeed, FractalNoiseSettings settings) {
        return createClimateSampler(worldSeed, TEMPERATURE_SEED_SALT, settings);
    }

    public static NoiseSampler2D createHumidity(long worldSeed, FractalNoiseSettings settings) {
        return createClimateSampler(worldSeed, HUMIDITY_SEED_SALT, settings);
    }

    public static NoiseSampler2D createContinentalness(long worldSeed, FractalNoiseSettings settings) {
        return createClimateSampler(worldSeed, CONTINENTALNESS_SEED_SALT, settings);
    }

    private static NoiseSampler2D createClimateSampler(long worldSeed, long salt, FractalNoiseSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Noise settings cannot be null.");

        return new FastSampler2D(SeedMixer.mixToInt(worldSeed, salt), FastNoiseLite.NoiseType.OpenSimplex2S, FastNoiseLite.FractalType.FBm, settings.frequency(), settings.octaves(), settings.lacunarity(), settings.gain());
    }

    public static NoiseSampler2D CreateTerrainHeight(int seed, FractalNoiseSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Noise settings cannot be null.");

        return new FastSampler2D(seed, FastNoiseLite.NoiseType.OpenSimplex2, FastNoiseLite.FractalType.FBm, settings.frequency(), settings.octaves(), settings.lacunarity(), settings.gain());
    }

    public static NoiseSampler2D CreateTerrainDetail(int seed, FractalNoiseSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Noise settings cannot be null.");

        return new FastSampler2D(seed, FastNoiseLite.NoiseType.OpenSimplex2S, FastNoiseLite.FractalType.FBm, settings.frequency(), settings.octaves(), settings.lacunarity(), settings.gain());
    }

    public static NoiseSampler2D CreateTerrainBlend(int seed, FractalNoiseSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Noise settings cannot be null.");

        return new FastSampler2D(seed, FastNoiseLite.NoiseType.OpenSimplex2, FastNoiseLite.FractalType.FBm, settings.frequency(), settings.octaves(), settings.lacunarity(), settings.gain());
    }

}
