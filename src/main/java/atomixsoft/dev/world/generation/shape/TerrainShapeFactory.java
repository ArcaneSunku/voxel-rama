package atomixsoft.dev.world.generation.shape;

import atomixsoft.dev.noise.NoiseSampler2D;
import atomixsoft.dev.noise.NoiseSamplers;
import atomixsoft.dev.world.generation.TerrainGenerationSettings;

public final class TerrainShapeFactory {

    private static final long TERRAIN_NOISE_SALT = 0x68BC21EBL;
    private static final long DETAIL_NOISE_SALT = 0x02E5BE93L;
    private static final long BLEND_NOISE_SALT = 0x7F4A7C15L;

    private TerrainShapeFactory() {}

    public static TerrainShape Create(long worldSeed, TerrainGenerationSettings settings) {
        if (settings == null)
            throw new IllegalArgumentException("Terrain generation settings cannot be null.");

        NoiseSampler2D terrainNoise = NoiseSamplers.CreateTerrainHeight(deriveIntSeed(worldSeed, TERRAIN_NOISE_SALT), settings.terrainNoise());
        NoiseSampler2D detailNoise = NoiseSamplers.CreateTerrainDetail(deriveIntSeed(worldSeed, DETAIL_NOISE_SALT), settings.detailNoise());

        return switch (settings.terrainShape()) {
            case PLAINS -> createPlains(settings, terrainNoise, detailNoise);

            case ROLLING_HILLS -> createRollingHills(settings, terrainNoise, detailNoise);

            case MOUNTAINS -> createMountains(settings, terrainNoise, detailNoise);

            case BLENDED -> createBlended(worldSeed, settings, terrainNoise, detailNoise);
        };
    }

    private static TerrainShape createPlains(TerrainGenerationSettings settings, NoiseSampler2D terrainNoise, NoiseSampler2D detailNoise) {
        return new PlainsTerrainShape(settings.baseHeight(), settings.terrainHeightRange(), settings.detailHeightRange(), terrainNoise, detailNoise);
    }

    private static TerrainShape createRollingHills(TerrainGenerationSettings settings, NoiseSampler2D terrainNoise, NoiseSampler2D detailNoise) {
        return new RollingHillsTerrainShape(settings.baseHeight(), settings.terrainHeightRange(), settings.detailHeightRange(), terrainNoise, detailNoise);
    }

    private static TerrainShape createMountains(TerrainGenerationSettings settings, NoiseSampler2D terrainNoise, NoiseSampler2D detailNoise) {
        return new MountainTerrainShape(settings.baseHeight(), settings.terrainHeightRange(), settings.detailHeightRange(), terrainNoise, detailNoise);
    }

    private static TerrainShape createBlended(long worldSeed, TerrainGenerationSettings settings, NoiseSampler2D terrainNoise, NoiseSampler2D detailNoise) {
        TerrainShape rollingHills = createRollingHills(settings, terrainNoise, detailNoise);
        TerrainShape mountains = createMountains(settings, terrainNoise, detailNoise);

        NoiseSampler2D blendNoise = NoiseSamplers.CreateTerrainBlend(deriveIntSeed(worldSeed, BLEND_NOISE_SALT), settings.blendNoise());
        return new BlendedTerrainShape(rollingHills, mountains, blendNoise);
    }

    private static int deriveIntSeed(long worldSeed, long salt) {
        long value = worldSeed ^ salt;

        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        value ^= value >>> 33;

        return Long.hashCode(value);
    }
}