package atomixsoft.dev.world.render;

import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;
import atomixsoft.dev.world.chunk.mesh.ChunkMesh;

public class ChunkModel {

    private final ChunkPosition m_Position;
    private final Chunk m_Chunk;
    private final ChunkMesh m_Mesh;

    public ChunkModel(ChunkPosition position, Chunk chunk) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        if (chunk == null)
            throw new IllegalArgumentException("Chunk cannot be null.");

        m_Position = position;
        m_Chunk = chunk;
        m_Mesh = new ChunkMesh();
    }

    public void dispose() {
        m_Mesh.dispose();
    }

    public ChunkPosition getPosition() {
        return m_Position;
    }

    public Chunk getChunk() {
        return m_Chunk;
    }

    public ChunkMesh getMesh() {
        return m_Mesh;
    }

}
