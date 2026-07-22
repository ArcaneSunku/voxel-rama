package atomixsoft.dev.world.generation;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.biome.BiomeDefinition;
import atomixsoft.dev.world.biome.BiomeSampler;
import atomixsoft.dev.world.biome.ClimateSample;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

public class WorldGenerator {

    private final TerrainGenerator m_TerrainGen;
    private final BiomeSampler m_BiomeSampler;

    public WorldGenerator(TerrainGenerator terrainGen, BiomeSampler biomeSampler) {
        if (terrainGen == null)
            throw new IllegalArgumentException("Terrain generator cannot be null.");

        if(biomeSampler == null)
            throw new IllegalArgumentException("Biome sampler cannot be null.");

        m_TerrainGen = terrainGen;
        m_BiomeSampler = biomeSampler;
    }

    public Chunk generateChunk(ChunkPosition position) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        return m_TerrainGen.generateChunk(position);
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
                    ChunkPosition pos = new ChunkPosition(chunkX, chunkY, chunkZ);
                    Chunk chunk = m_TerrainGen.generateChunk(pos);

                    world.addChunk(pos, chunk);
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

    public ClimateSample sampleClimate(int worldX, int worldZ) {
        return m_BiomeSampler.sampleClimate(worldX, worldZ);
    }

    public BiomeDefinition getBiome(int worldX, int worldZ) {
        return m_BiomeSampler.sampleBiome(worldX, worldZ);
    }

    private void validateRange(int minimum, int maximum, String axisName) {
        if (minimum > maximum)
            throw new IllegalArgumentException("Minimum " + axisName + " chunk coordinate cannot exceed maximum.");
    }

}
