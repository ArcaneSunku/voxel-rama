package atomixsoft.dev.world.gen;

import atomixsoft.dev.noise.FractalNoiseSettings;

public record TerrainGenerationSettings(int baseHeight, int terrainHeightRange, int detailHeightRange, int dirtDepth,
                                        FractalNoiseSettings terrainNoise, FractalNoiseSettings detailNoise) {

    public TerrainGenerationSettings {
        if (terrainHeightRange < 0)
            throw new IllegalArgumentException("Terrain height range cannot be negative.");

        if (detailHeightRange < 0)
            throw new IllegalArgumentException("Detail height range cannot be negative.");

        if (dirtDepth < 0)
            throw new IllegalArgumentException("Dirt depth cannot be negative.");

        if (terrainNoise == null)
            throw new IllegalArgumentException("Terrain noise settings cannot be null.");

        if (detailNoise == null)
            throw new IllegalArgumentException("Detail noise settings cannot be null.");
    }

    public int minimumSurfaceHeight() {
        return baseHeight - terrainHeightRange - detailHeightRange;
    }

    public int maximumSurfaceHeight() {
        return baseHeight + terrainHeightRange + detailHeightRange;
    }

}