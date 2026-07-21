package atomixsoft.dev.world.chunk.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

public class ChunkMesh {

    private int m_VAO, m_VBO, m_EBO;

    private int m_IndexCount;
    private boolean m_Disposed;

    public ChunkMesh() {
        createBuffers();
    }

    public ChunkMesh(ChunkMeshData meshData) {
        this();
        upload(meshData);
    }

    public void draw() {
        ensureNotDisposed();
        if(m_IndexCount == 0)
            return;

        glBindVertexArray(m_VAO);
        glDrawElements(GL_TRIANGLES, m_IndexCount, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
    }

    public void upload(ChunkMeshData meshData) {
        ensureNotDisposed();
        if(meshData == null)
            throw new IllegalArgumentException("ChunkMeshData cannot be null!");

        float[] vertices = meshData.getVertices();
        int[] indices = meshData.getIndices();

        glBindVertexArray(m_VAO);

        glBindBuffer(GL_ARRAY_BUFFER, m_VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int stride = ChunkMesher.COMPONENTS_PER_VERTEX * Float.BYTES;
        glVertexAttribPointer(0, ChunkMesher.POS_COMPONENT_COUNT, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        long colorOffset = (long) ChunkMesher.POS_COMPONENT_COUNT * Float.BYTES;
        glVertexAttribPointer(1, ChunkMesher.COL_COMPONENT_COUNT, GL_FLOAT, false, stride, colorOffset);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        m_IndexCount = meshData.getIndexCount();
    }

    public void dispose() {
        if(m_Disposed) return;

        glDeleteBuffers(m_EBO);
        glDeleteBuffers(m_VBO);
        glDeleteVertexArrays(m_VAO);

        m_IndexCount = 0;
        m_Disposed = true;
    }

    public int getIndexCount() {
        return m_IndexCount;
    }

    public int getTriangleCount() {
        return m_IndexCount / 3;
    }

    public boolean isEmpty() {
        return m_IndexCount == 0;
    }

    public boolean isDisposed() {
        return m_Disposed;
    }

    private void createBuffers() {
        m_VAO = glGenVertexArrays();
        m_VBO = glGenBuffers();
        m_EBO = glGenBuffers();

        if(m_VAO == 0 || m_VBO == 0 || m_EBO == 0) {
            dispose();
            throw new IllegalStateException("Failed to create Chunk Mesh OpenGL buffers!");
        }
    }

    private void ensureNotDisposed() {
        if(m_Disposed)
            throw new IllegalStateException("ChunkMesh already disposed");
    }

}
