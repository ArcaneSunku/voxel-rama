package atomixsoft.dev.world.generation;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.chunk.ChunkPosition;

public interface TerrainGenerator {

    void generateChunk(World world, ChunkPosition position);

}
