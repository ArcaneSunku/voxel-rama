package atomixsoft.dev.noise;

public final class NoiseSamplers {

    private NoiseSamplers() {
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

}
