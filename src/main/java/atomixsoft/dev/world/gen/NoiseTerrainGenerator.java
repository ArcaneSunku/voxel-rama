package atomixsoft.dev.world.gen;

import atomixsoft.dev.noise.NoiseSampler2D;
import atomixsoft.dev.noise.NoiseSamplers;
import atomixsoft.dev.world.World;
import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.block.Blocks;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

public final class NoiseTerrainGenerator implements TerrainGenerator {

    private final long m_Seed;
    private final TerrainGenerationSettings m_Settings;

    private final NoiseSampler2D m_HeightNoise;
    private final NoiseSampler2D m_DetailNoise;

    public NoiseTerrainGenerator(long seed) {
        this(seed, TerrainGenerationPresets.ROLLING_HILLS);
    }

    public NoiseTerrainGenerator(long seed, TerrainGenerationSettings settings) {
        if(settings == null)
            throw new IllegalArgumentException("Settings cannot be null");

        m_Seed = seed;
        m_Settings = settings;

        m_HeightNoise = NoiseSamplers.CreateTerrainHeight(deriveIntSeed(seed, 0x68BC21EBL), settings.terrainNoise());
        m_DetailNoise = NoiseSamplers.CreateTerrainDetail(deriveIntSeed(seed, 0x02E5BE93L), settings.detailNoise());
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

    @Override
    public void generateChunk(World world, ChunkPosition position) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null.");

        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        Chunk chunk;
        if (world.hasChunk(position))
            chunk = world.requireChunk(position);
        else
            chunk = world.createChunk(position);

        int worldOriginX = position.getWorldBlockOriginX();
        int worldOriginY = position.getWorldBlockOriginY();
        int worldOriginZ = position.getWorldBlockOriginZ();

        for (int localZ = 0; localZ < Chunk.SIZE; localZ++) {
            for (int localX = 0; localX < Chunk.SIZE; localX++) {
                int worldX = worldOriginX + localX;

                int worldZ = worldOriginZ + localZ;

                int surfaceHeight = calculateSurfaceHeight(worldX, worldZ);

                generateColumn(chunk, localX, localZ, worldOriginY, surfaceHeight);
            }
        }

        chunk.markMesh(true);
    }

    public int calculateSurfaceHeight(int worldX, int worldZ) {
        float terrainNoise = m_HeightNoise.sample(worldX, worldZ);
        float detailNoise = m_DetailNoise.sample(worldX, worldZ);

        int terrainHeight = Math.round(terrainNoise * m_Settings.terrainHeightRange());
        int detailHeight = Math.round(detailNoise * m_Settings.detailHeightRange());

        return m_Settings.baseHeight() + terrainHeight + detailHeight;
    }

    public long getSeed() {
        return m_Seed;
    }

    public TerrainGenerationSettings getSettings() {
        return m_Settings;
    }

    private void generateColumn(Chunk chunk, int localX, int localZ, int worldOriginY, int surfaceHeight) {
        for (int localY = 0; localY < Chunk.SIZE; localY++) {
            int worldY = worldOriginY + localY;

            Block block = selectBlock(worldY, surfaceHeight);
            chunk.setBlock(localX, localY, localZ, block);
        }
    }

    private Block selectBlock(int worldY, int surfaceHeight) {
        if (worldY > surfaceHeight)
            return Blocks.AIR;

        if (worldY == surfaceHeight)
            return Blocks.GRASS;

        if (worldY >= surfaceHeight - m_Settings.dirtDepth())
            return Blocks.DIRT;

        return Blocks.STONE;
    }

}