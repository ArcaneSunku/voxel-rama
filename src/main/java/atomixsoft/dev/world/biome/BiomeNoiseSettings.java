package atomixsoft.dev.world.biome;

import atomixsoft.dev.noise.FractalNoiseSettings;

public record BiomeNoiseSettings(FractalNoiseSettings temperatureNoise, FractalNoiseSettings humidityNoise,
                                 FractalNoiseSettings continentalnessNoise) {

    public BiomeNoiseSettings {
        if (temperatureNoise == null)
            throw new IllegalArgumentException("Temperature noise settings cannot be null.");

        if (humidityNoise == null)
            throw new IllegalArgumentException("Humidity noise settings cannot be null.");

        if (continentalnessNoise == null)
            throw new IllegalArgumentException("Continentalness noise settings cannot be null.");
    }
}