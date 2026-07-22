package atomixsoft.dev.world.generation;

import atomixsoft.dev.world.WorldProperties;

import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.block.Blocks;

import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

import atomixsoft.dev.world.generation.shape.TerrainShape;
import atomixsoft.dev.world.generation.shape.TerrainShapeFactory;

public final class NoiseTerrainGenerator implements TerrainGenerator {

    private final long m_Seed;
    private final TerrainGenerationSettings m_Settings;

    private final TerrainShape m_TerrainShape;

    public NoiseTerrainGenerator(WorldProperties properties) {
        this(requireProperties(properties).seedValue(), TerrainGenerationPresets.Get(properties.terrainPreset()));
    }

    public NoiseTerrainGenerator(long seed) {
        this(seed, TerrainGenerationPresets.ROLLING_HILLS);
    }

    public NoiseTerrainGenerator(long seed, TerrainGenerationSettings settings) {
        if(settings == null)
            throw new IllegalArgumentException("Settings cannot be null");

        m_Seed = seed;
        m_Settings = settings;

        m_TerrainShape = TerrainShapeFactory.Create(seed, settings);
    }

    @Override
    public Chunk generateChunk(ChunkPosition position) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        Chunk chunk = new Chunk();

        int chunkWorldX = position.x() * Chunk.SIZE;
        int chunkWorldY = position.y() * Chunk.SIZE;
        int chunkWorldZ = position.z() * Chunk.SIZE;

        for (int localZ = 0; localZ < Chunk.SIZE; localZ++) {
            int worldZ = chunkWorldZ + localZ;

            for (int localX = 0; localX < Chunk.SIZE; localX++) {
                int worldX = chunkWorldX + localX;
                int surfaceHeight = calculateSurfaceHeight(worldX, worldZ);

                for(int localY = 0; localY < Chunk.SIZE; localY++) {
                    int worldY = chunkWorldY + localY;

                    short blockId = selectBlock(worldY, surfaceHeight).getId();
                    if(blockId == Blocks.AIR.getId())
                        continue;

                    chunk.setBlockId(localX, localY, localZ, blockId);
                }
            }
        }

        chunk.markMesh(true);
        return chunk;
    }

    public int calculateSurfaceHeight(int worldX, int worldZ) {
        return m_TerrainShape.calculateSurfaceHeight(worldX, worldZ);
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

    private static WorldProperties requireProperties(WorldProperties properties) {
        if (properties == null)
            throw new IllegalArgumentException("World properties cannot be null.");

        return properties;
    }

}