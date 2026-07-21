package atomixsoft.dev.noise;

public final class NoiseSamplers {

    private NoiseSamplers() {}

    public static NoiseSampler2D CreateTerrainHeight(int seed) {
        return new FastSampler2D(seed, FastNoiseLite.NoiseType.OpenSimplex2, FastNoiseLite.FractalType.FBm, 0.0085f, 5, 2.0f, 0.5f);
    }

    public static NoiseSampler2D CreateTerrainDetail(int seed) {
        return new FastSampler2D(seed, FastNoiseLite.NoiseType.OpenSimplex2S, FastNoiseLite.FractalType.FBm, 0.0275f, 3, 2.0f, 0.5f);
    }

}
