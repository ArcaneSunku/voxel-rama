package atomixsoft.dev.world.gen.shape;

import atomixsoft.dev.noise.NoiseSampler2D;

public final class BlendedTerrainShape implements TerrainShape {

    private final TerrainShape m_LowTerrain;
    private final TerrainShape m_HighTerrain;

    private final NoiseSampler2D m_BlendNoise;

    public BlendedTerrainShape(TerrainShape lowTerrain, TerrainShape highTerrain, NoiseSampler2D blendNoise) {
        if (lowTerrain == null)
            throw new IllegalArgumentException("Low terrain shape cannot be null.");

        if (highTerrain == null)
            throw new IllegalArgumentException("High terrain shape cannot be null.");

        if (blendNoise == null)
            throw new IllegalArgumentException("Blend noise cannot be null.");

        m_LowTerrain = lowTerrain;
        m_HighTerrain = highTerrain;
        m_BlendNoise = blendNoise;
    }

    private static float clamp01(float value) {
        return Math.clamp(value, 0.0f, 1.0f);
    }

    @Override
    public int calculateSurfaceHeight(int worldX, int worldZ) {
        int lowHeight = m_LowTerrain.calculateSurfaceHeight(worldX, worldZ);
        int highHeight = m_HighTerrain.calculateSurfaceHeight(worldX, worldZ);

        float blendSample = m_BlendNoise.sample(worldX, worldZ);
        float blend = clamp01(blendSample * 0.5f + 0.5f);
        float smoothBlend = blend * blend * (3.0f - 2.0f * blend);

        return Math.round(lowHeight + (highHeight - lowHeight) * smoothBlend);
    }

}