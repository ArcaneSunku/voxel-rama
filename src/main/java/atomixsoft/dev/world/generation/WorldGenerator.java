package atomixsoft.dev.world.generation;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.chunk.ChunkPosition;

public class WorldGenerator {

    private final TerrainGenerator m_TerrainGen;

    public WorldGenerator(TerrainGenerator terrainGen) {
        if (terrainGen == null)
            throw new IllegalArgumentException("Terrain generator cannot be null.");

        m_TerrainGen = terrainGen;
    }

    public void generateChunk(World world, ChunkPosition position) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null.");

        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        if (world.hasChunk(position))
            return;

        m_TerrainGen.generateChunk(world, position);
    }

    public void generateRegion(World world, int minimumChunkX, int maximumChunkX, int minimumChunkY, int maximumChunkY, int minimumChunkZ, int maximumChunkZ) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null.");

        validateRange(minimumChunkX, maximumChunkX, "X");
        validateRange(minimumChunkY, maximumChunkY, "Y");
        validateRange(minimumChunkZ, maximumChunkZ, "Z");

        for (int chunkY = minimumChunkY; chunkY <= maximumChunkY; chunkY++) {
            for (int chunkZ = minimumChunkZ; chunkZ <= maximumChunkZ; chunkZ++) {
                for (int chunkX = minimumChunkX; chunkX <= maximumChunkX; chunkX++) {
                    m_TerrainGen.generateChunk(world, new ChunkPosition(chunkX, chunkY, chunkZ));
                }
            }
        }
    }

    public void generateHorizontalRegion(World world, int minimumChunkX, int maximumChunkX, int chunkY, int minimumChunkZ, int maximumChunkZ) {
        generateRegion(world, minimumChunkX, maximumChunkX, chunkY, chunkY, minimumChunkZ, maximumChunkZ);
    }

    public void generateSquare(World world, int centerChunkX, int centerChunkZ, int chunkY, int radius) {
        if (radius < 0)
            throw new IllegalArgumentException("Generation radius cannot be negative.");

        generateHorizontalRegion(world, centerChunkX - radius, centerChunkX + radius, chunkY, centerChunkZ - radius, centerChunkZ + radius);
    }

    private void validateRange(int minimum, int maximum, String axisName) {
        if (minimum > maximum)
            throw new IllegalArgumentException("Minimum " + axisName + " chunk coordinate cannot exceed maximum.");
    }

}
