package atomixsoft.dev.world.gen.shape;

import atomixsoft.dev.noise.NoiseSampler2D;

public final class RollingHillsTerrainShape implements TerrainShape {

    private final int m_BaseHeight;
    private final int m_TerrainHeightRange;
    private final int m_DetailHeightRange;

    private final NoiseSampler2D m_TerrainNoise;
    private final NoiseSampler2D m_DetailNoise;

    public RollingHillsTerrainShape(int baseHeight, int terrainHeightRange, int detailHeightRange, NoiseSampler2D terrainNoise, NoiseSampler2D detailNoise) {
        if (terrainHeightRange < 0)
            throw new IllegalArgumentException("Terrain height range cannot be negative.");

        if (detailHeightRange < 0)
            throw new IllegalArgumentException("Detail height range cannot be negative.");

        if (terrainNoise == null)
            throw new IllegalArgumentException("Terrain noise cannot be null.");

        if (detailNoise == null)
            throw new IllegalArgumentException("Detail noise cannot be null.");

        m_BaseHeight = baseHeight;
        m_TerrainHeightRange = terrainHeightRange;
        m_DetailHeightRange = detailHeightRange;

        m_TerrainNoise = terrainNoise;
        m_DetailNoise = detailNoise;
    }

    @Override
    public int calculateSurfaceHeight(int worldX, int worldZ) {
        float terrainSample = m_TerrainNoise.sample(worldX, worldZ);
        float detailSample = m_DetailNoise.sample(worldX, worldZ);

        int terrainHeight = Math.round(terrainSample * m_TerrainHeightRange);
        int detailHeight = Math.round(detailSample * m_DetailHeightRange);

        return m_BaseHeight + terrainHeight + detailHeight;
    }

}