package atomixsoft.dev.world.gen;

import atomixsoft.dev.noise.FractalNoiseSettings;
import atomixsoft.dev.world.gen.shape.TerrainShapeType;

public final class TerrainGenerationPresets {

    private static final FractalNoiseSettings BROAD_TERRAIN_NOISE = new FractalNoiseSettings(0.0085f, 5, 2.0f, 0.5f);
    private static final FractalNoiseSettings DETAIL_NOISE = new FractalNoiseSettings(0.0275f, 3, 2.0f, 0.5f);
    private static final FractalNoiseSettings REGION_BLEND_NOISE = new FractalNoiseSettings(0.0015f, 3, 2.0f, 0.5f);

    public static final TerrainGenerationSettings PLAINS = new TerrainGenerationSettings(TerrainShapeType.PLAINS, 7, 4, 1, 3, BROAD_TERRAIN_NOISE, DETAIL_NOISE, REGION_BLEND_NOISE);
    public static final TerrainGenerationSettings ROLLING_HILLS = new TerrainGenerationSettings(TerrainShapeType.ROLLING_HILLS, 7, 5, 2, 3, BROAD_TERRAIN_NOISE, DETAIL_NOISE, REGION_BLEND_NOISE);
    public static final TerrainGenerationSettings MOUNTAINS = new TerrainGenerationSettings(TerrainShapeType.MOUNTAINS, 5, 10, 2, 3, BROAD_TERRAIN_NOISE, DETAIL_NOISE, REGION_BLEND_NOISE);
    public static final TerrainGenerationSettings BLENDED = new TerrainGenerationSettings(TerrainShapeType.BLENDED, 6, 9, 2, 3, BROAD_TERRAIN_NOISE, DETAIL_NOISE, REGION_BLEND_NOISE);

    private TerrainGenerationPresets() {
    }

    public static TerrainGenerationSettings Get(TerrainPresetId presetId) {
        if (presetId == null)
            throw new IllegalArgumentException("Terrain preset ID cannot be null.");

        return switch (presetId) {
            case PLAINS -> PLAINS;
            case ROLLING_HILLS -> ROLLING_HILLS;
            case MOUNTAINS -> MOUNTAINS;
            case BLENDED -> BLENDED;
        };
    }

}
