package atomixsoft.dev.world.gen;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.block.Blocks;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

public final class SimpleTerrainGenerator implements TerrainGenerator {

    private static final int BASE_HEIGHT = 6;
    private static final int DIRT_DEPTH = 3;

    private static final double LARGE_HILL_FREQUENCY = 0.075;
    private static final double SMALL_HILL_FREQUENCY = 0.165;
    private static final double LARGE_HILL_AMPLITUDE = 3.0;
    private static final double SMALL_HILL_AMPLITUDE = 1.5;

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
        double largeHills = Math.sin(worldX * LARGE_HILL_FREQUENCY) * LARGE_HILL_AMPLITUDE;
        double crossingHills = Math.cos(worldZ * LARGE_HILL_FREQUENCY) * LARGE_HILL_AMPLITUDE;
        double smallDetails = Math.sin((worldX + worldZ) * SMALL_HILL_FREQUENCY) * SMALL_HILL_AMPLITUDE;

        return BASE_HEIGHT + (int) Math.round(largeHills + crossingHills + smallDetails);
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

        if (worldY >= surfaceHeight - DIRT_DEPTH)
            return Blocks.DIRT;

        return Blocks.STONE;
    }
}