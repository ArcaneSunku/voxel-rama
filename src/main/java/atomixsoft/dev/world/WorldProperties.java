package atomixsoft.dev.world;

import atomixsoft.dev.world.generation.TerrainPresetId;

public record WorldProperties(String name, WorldSeed seed, TerrainPresetId terrainPreset, int generatorVersion) {

    public WorldProperties {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("World name cannot be blank.");

        if (seed == null)
            throw new IllegalArgumentException("World seed cannot be null.");

        if (terrainPreset == null)
            throw new IllegalArgumentException("Terrain preset cannot be null.");

        if (generatorVersion < 1)
            throw new IllegalArgumentException("Generator version must be at least one.");

        name = name.trim();
    }

    public long seedValue() {
        return seed.value();
    }

}
