package atomixsoft.dev.world.biome;

public final class BiomeSamplers {

    private BiomeSamplers() {}

    public static BiomeSampler createDefault(long worldSeed) {
        return new NoiseBiomeSampler(worldSeed, BiomeNoisePresets.DEFAULT);
    }
}