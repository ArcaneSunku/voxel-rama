package atomixsoft.dev.noise;

public record FractalNoiseSettings(float frequency, int octaves, float lacunarity, float gain) {

    public FractalNoiseSettings {
        if (!Float.isFinite(frequency) || frequency <= 0.0f)
            throw new IllegalArgumentException("Noise frequency must be finite and greater than zero.");

        if (octaves < 1)
            throw new IllegalArgumentException("Noise octave count must be at least one.");

        if (!Float.isFinite(lacunarity) || lacunarity <= 0.0f)
            throw new IllegalArgumentException("Noise lacunarity must be finite and greater than zero.");

        if (!Float.isFinite(gain) || gain < 0.0f || gain > 1.0f)
            throw new IllegalArgumentException("Noise gain must be between zero and one.");
    }
}
