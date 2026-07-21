package atomixsoft.dev.world.gen;

import atomixsoft.dev.noise.FractalNoiseSettings;

public final class TerrainGenerationPresets {

    public static final TerrainGenerationSettings ROLLING_HILLS = new TerrainGenerationSettings(
            7, 5, 2, 3,
            new FractalNoiseSettings(0.0085f, 5, 2.0f, 0.5f),
            new FractalNoiseSettings(0.0275f, 3, 2.0f, 0.5f));

    private TerrainGenerationPresets() {}

}
