package atomixsoft.dev.world.chunk.mesh;

public class ChunkMeshData {

    private final float[] m_Vertices;
    private final int[] m_Indices;

    public ChunkMeshData(float[] vertices, int[] indices) {
        if(vertices == null)
            throw new IllegalArgumentException("Vertices cannot be null.");

        if(indices == null)
            throw new IllegalArgumentException("Indices cannot be null.");

        m_Vertices = vertices;
        m_Indices = indices;
    }

    public float[] getVertices() {
        return m_Vertices;
    }

    public int[] getIndices() {
        return m_Indices;
    }

    public int getVertexCount() {
        return m_Vertices.length / ChunkMesher.COMPONENTS_PER_VERTEX;
    }

    public int getIndexCount() {
        return m_Indices.length;
    }

    public int getTriangleCount() {
        return m_Indices.length / 3;
    }

    public boolean isEmpty() {
        return m_Indices.length == 0;
    }

}
