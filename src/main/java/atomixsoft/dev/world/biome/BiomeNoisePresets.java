package atomixsoft.dev.world.biome;

import atomixsoft.dev.noise.FractalNoiseSettings;

public final class BiomeNoisePresets {

    public static final BiomeNoiseSettings DEFAULT = new BiomeNoiseSettings(new FractalNoiseSettings(0.00085f, 3, 2.0f, 0.50f),
            new FractalNoiseSettings(0.00095f, 3, 2.0f, 0.50f), new FractalNoiseSettings(0.00045f, 4, 2.0f, 0.50f));

    private BiomeNoisePresets() {}

}