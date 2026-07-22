package atomixsoft.dev.world.biome;

public record ClimateSample(float temperature, float humidity, float continentalness) {

    public ClimateSample {
        temperature = validateNormalized(temperature, "Temperature");
        humidity = validateNormalized(humidity, "Humidity");
        continentalness = validateNormalized(continentalness, "Continentalness");
    }

    private static float validateNormalized(float value, String name) {
        if (!Float.isFinite(value))
            throw new IllegalArgumentException(name + " must be finite.");

        if (value < 0.0f || value > 1.0f)
            throw new IllegalArgumentException(name + " must be between zero and one.");

        return value;
    }
}
