package atomixsoft.dev.world.gen;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.chunk.ChunkPosition;

public interface TerrainGenerator {

    void generateChunk(World world, ChunkPosition position);

}
