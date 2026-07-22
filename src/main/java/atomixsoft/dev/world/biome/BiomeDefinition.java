package atomixsoft.dev.world.biome;

public record BiomeDefinition(BiomeId id, String name, float targetTemperature, float targetHumidity,
                              float targetContinentalness, float temperatureWeight, float humidityWeight,
                              float continentalnessWeight) {

    public BiomeDefinition {
        if (id == null)
            throw new IllegalArgumentException("Biome ID cannot be null.");

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Biome name cannot be blank.");

        name = name.trim();

        validateNormalized(targetTemperature, "Target temperature");
        validateNormalized(targetHumidity, "Target humidity");
        validateNormalized(targetContinentalness, "Target continentalness");

        validateWeight(temperatureWeight, "Temperature weight");
        validateWeight(humidityWeight, "Humidity weight");
        validateWeight(continentalnessWeight, "Continentalness weight");
    }

    private static void validateNormalized(float value, String name) {
        if (!Float.isFinite(value))
            throw new IllegalArgumentException(name + " must be finite.");

        if (value < 0.0f || value > 1.0f)
            throw new IllegalArgumentException(name + " must be between zero and one.");
    }

    private static void validateWeight(float value, String name) {
        if (!Float.isFinite(value))
            throw new IllegalArgumentException(name + " must be finite.");

        if (value <= 0.0f)
            throw new IllegalArgumentException(name + " must be greater than zero.");
    }

    public float calculateClimateDistance(ClimateSample climate) {
        if (climate == null)
            throw new IllegalArgumentException("Climate sample cannot be null.");

        float temperatureDifference = climate.temperature() - targetTemperature;
        float humidityDifference = climate.humidity() - targetHumidity;
        float continentalnessDifference = climate.continentalness() - targetContinentalness;

        return temperatureDifference * temperatureDifference * temperatureWeight + humidityDifference * humidityDifference * humidityWeight + continentalnessDifference * continentalnessDifference * continentalnessWeight;
    }
}