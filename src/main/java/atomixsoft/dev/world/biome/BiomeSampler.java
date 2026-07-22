package atomixsoft.dev.world.biome;

public interface BiomeSampler {

    ClimateSample sampleClimate(int worldX, int worldZ);

    BiomeDefinition sampleBiome(int worldX, int worldZ);

}
