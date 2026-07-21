package atomixsoft.dev.world.render;

import atomixsoft.dev.graphics.Camera;
import atomixsoft.dev.graphics.Shader;
import atomixsoft.dev.world.World;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;
import atomixsoft.dev.world.chunk.mesh.ChunkMesh;
import atomixsoft.dev.world.chunk.mesh.ChunkMeshData;
import atomixsoft.dev.world.chunk.mesh.ChunkMesher;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class WorldRenderer {

    private final World m_World;

    private final Map<ChunkPosition, ChunkMesh> m_ChunkMeshes;
    private final Matrix4f m_ModelMatrix;

    private boolean m_Disposed;

    public WorldRenderer(World world) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null.");

        m_World = world;
        m_ChunkMeshes = new HashMap<>();
        m_ModelMatrix = new Matrix4f();
    }

    public void update() {
        ensureNotDisposed();

        synchronizeChunkMeshes();
        rebuildDirtyChunkMeshes();
    }

    public void render(Shader shader, Camera camera) {
        ensureNotDisposed();

        if (shader == null)
            throw new IllegalArgumentException("Shader cannot be null.");

        if (camera == null)
            throw new IllegalArgumentException("Camera cannot be null.");

        shader.bind();

        shader.setUniform("u_View", camera.getViewMatrix());
        shader.setUniform("u_Projection", camera.getProjectionMatrix());

        for (Map.Entry<ChunkPosition, ChunkMesh> entry : m_ChunkMeshes.entrySet()) {
            ChunkMesh mesh = entry.getValue();

            if (mesh.isEmpty())
                continue;

            ChunkPosition position = entry.getKey();
            m_ModelMatrix.identity().translation(position.getWorldBlockOriginX(), position.getWorldBlockOriginY(), position.getWorldBlockOriginZ());

            shader.setUniform("u_Model", m_ModelMatrix);
            mesh.draw();
        }

        shader.unbind();
    }

    public void dispose() {
        if (m_Disposed)
            return;

        for (ChunkMesh mesh : m_ChunkMeshes.values())
            mesh.dispose();

        m_ChunkMeshes.clear();
        m_Disposed = true;
    }

    public int getRenderedChunkCount() {
        return m_ChunkMeshes.size();
    }

    public int getTotalTriangleCount() {
        int triangleCount = 0;
        for (ChunkMesh mesh : m_ChunkMeshes.values())
            triangleCount += mesh.getTriangleCount();

        return triangleCount;
    }

    public boolean hasChunkMesh(ChunkPosition position) {
        if (position == null)
            return false;

        return m_ChunkMeshes.containsKey(position);
    }

    public boolean isDisposed() {
        return m_Disposed;
    }

    private void synchronizeChunkMeshes() {
        removeMissingChunkMeshes();
        createMissingChunkMeshes();
    }

    private void removeMissingChunkMeshes() {
        Iterator<Map.Entry<ChunkPosition, ChunkMesh>> iterator = m_ChunkMeshes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ChunkPosition, ChunkMesh> entry = iterator.next();

            if (m_World.hasChunk(entry.getKey()))
                continue;

            entry.getValue().dispose();
            iterator.remove();
        }
    }

    private void createMissingChunkMeshes() {
        for (ChunkPosition position : m_World.getChunks().keySet()) {
            if (m_ChunkMeshes.containsKey(position))
                continue;

            m_ChunkMeshes.put(position, new ChunkMesh());
        }
    }

    private void rebuildDirtyChunkMeshes() {
        for (Map.Entry<ChunkPosition, ChunkMesh> entry : m_ChunkMeshes.entrySet()) {
            ChunkPosition position = entry.getKey();
            Chunk chunk = m_World.getChunk(position);

            if (chunk == null || !chunk.isDirty())
                continue;

            ChunkMeshData meshData = ChunkMesher.Build(m_World, position);

            entry.getValue().upload(meshData);
            chunk.clean();
        }
    }

    private void ensureNotDisposed() {
        if (m_Disposed)
            throw new IllegalStateException("World renderer has already been disposed.");
    }
}