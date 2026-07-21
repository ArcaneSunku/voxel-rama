package atomixsoft.dev.noise;

public final class FastSampler2D implements NoiseSampler2D {

    private final FastNoiseLite m_Noise;

    public FastSampler2D(int seed, FastNoiseLite.NoiseType noiseType, FastNoiseLite.FractalType fractalType, float frequency, int octaves, float lacunarity, float gain) {
        validateFrequency(frequency);
        validateOctaves(octaves);
        validateLacunarity(lacunarity);
        validateGain(gain);

        if (noiseType == null)
            throw new IllegalArgumentException("Noise type cannot be null.");

        if (fractalType == null)
            throw new IllegalArgumentException("Fractal type cannot be null.");

        m_Noise = new FastNoiseLite(seed);

        m_Noise.SetNoiseType(noiseType);
        m_Noise.SetFractalType(fractalType);
        m_Noise.SetFrequency(frequency);
        m_Noise.SetFractalOctaves(octaves);
        m_Noise.SetFractalLacunarity(lacunarity);
        m_Noise.SetFractalGain(gain);
    }

    @Override
    public float sample(float x, float z) {
        return m_Noise.GetNoise(x, z);
    }

    private static void validateFrequency(float frequency) {
        if (!Float.isFinite(frequency) || frequency <= 0.0f)
            throw new IllegalArgumentException("Noise frequency must be finite and greater than zero.");
    }

    private static void validateOctaves(int octaves) {
        if (octaves < 1)
            throw new IllegalArgumentException("Noise octave count must be at least one.");
    }

    private static void validateLacunarity(float lacunarity) {
        if (!Float.isFinite(lacunarity) || lacunarity <= 0.0f)
            throw new IllegalArgumentException("Noise lacunarity must be finite and greater than zero.");
    }

    private static void validateGain(float gain) {
        if (!Float.isFinite(gain) || gain < 0.0f || gain > 1.0f)
            throw new IllegalArgumentException("Noise gain must be between zero and one.");
    }

}
