package atomixsoft.dev.world.biome;

import atomixsoft.dev.noise.NoiseSampler2D;
import atomixsoft.dev.noise.NoiseSamplers;

public final class NoiseBiomeSampler implements BiomeSampler {

    private final NoiseSampler2D m_TemperatureNoise;
    private final NoiseSampler2D m_HumidityNoise;
    private final NoiseSampler2D m_ContinentalnessNoise;

    public NoiseBiomeSampler(long worldSeed, BiomeNoiseSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Biome noise settings cannot be null.");

        m_TemperatureNoise = NoiseSamplers.createTemperature(worldSeed, settings.temperatureNoise());
        m_HumidityNoise = NoiseSamplers.createHumidity(worldSeed, settings.humidityNoise());
        m_ContinentalnessNoise = NoiseSamplers.createContinentalness(worldSeed, settings.continentalnessNoise());
    }

    private static float normalizeNoise(float value) {
        if (!Float.isFinite(value))
            throw new IllegalStateException("Noise sampler returned a non-finite value.");

        float normalized = value * 0.5f + 0.5f;
        return Math.clamp(normalized, 0.0f, 1.0f);
    }

    @Override
    public ClimateSample sampleClimate(int worldX, int worldZ) {
        float temperature = normalizeNoise(m_TemperatureNoise.sample(worldX, worldZ));
        float humidity = normalizeNoise(m_HumidityNoise.sample(worldX, worldZ));
        float continentalness = normalizeNoise(m_ContinentalnessNoise.sample(worldX, worldZ));

        return new ClimateSample(temperature, humidity, continentalness);
    }

    @Override
    public BiomeDefinition sampleBiome(int worldX, int worldZ) {
        ClimateSample climate = sampleClimate(worldX, worldZ);
        BiomeDefinition selectedBiome = null;

        float selectedDistance = Float.POSITIVE_INFINITY;
        for (BiomeDefinition biome : Biomes.values()) {
            float distance = biome.calculateClimateDistance(climate);

            if (distance < selectedDistance) {
                selectedBiome = biome;
                selectedDistance = distance;
            }
        }

        if (selectedBiome == null)
            throw new IllegalStateException("No biome definitions are registered.");

        return selectedBiome;
    }

}