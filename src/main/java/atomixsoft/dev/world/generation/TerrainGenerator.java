package atomixsoft.dev.world.generation;

import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

public interface TerrainGenerator {

    Chunk generateChunk(ChunkPosition position);

}
