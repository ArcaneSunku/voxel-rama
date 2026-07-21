package atomixsoft.dev.world.chunk.mesh;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;
import org.joml.Vector3f;

import java.util.Arrays;

public final class ChunkMesher {

    public static final int POS_COMPONENT_COUNT = 3;
    public static final int COL_COMPONENT_COUNT = 3;

    public static final int COMPONENTS_PER_VERTEX = POS_COMPONENT_COUNT + COL_COMPONENT_COUNT;

    private static final int VERTICES_PER_FACE = 4;
    private static final int INDICES_PER_FACE = 6;

    private static final int[] FACE_INDICES = {
            0, 1, 2,
            2, 3, 0
    };

    private ChunkMesher() {}

    public static ChunkMeshData Build(World world, ChunkPosition chunkPos) {
        if(world == null)
            throw new IllegalArgumentException("World cannot be null!");

        if(chunkPos == null)
            throw new IllegalArgumentException("Chunk position cannot be null!");

        Chunk chunk = world.requireChunk(chunkPos);
        FloatArrayBuilder vertices = new FloatArrayBuilder();
        IntArrayBuilder indices = new IntArrayBuilder();

        for(int y = 0; y < Chunk.SIZE; y++) {
            for(int z = 0; z < Chunk.SIZE; z++) {
                for(int x = 0; x < Chunk.SIZE; x++) {
                    Block block = chunk.getBlock(x, y, z);
                    if(!block.isSolid())
                        continue;

                    for(Face face : Face.values()) {
                        if(!isFaceVisible(world, chunkPos, x, y, z, face))
                            continue;

                        appendFace(vertices, indices, block, x, y, z, face);
                    }
                }
            }
        }

        return new ChunkMeshData(vertices.toArray(), indices.toArray());
    }

    private static void appendFace(FloatArrayBuilder vertices, IntArrayBuilder indices, Block block, int x, int y, int z, Face face) {
        int firstVertex = vertices.size() / COMPONENTS_PER_VERTEX;
        Vector3f color = new Vector3f(block.getColor()).mul(face.m_Shade);

        for(int vertex = 0; vertex < VERTICES_PER_FACE; vertex++) {
            int cornerOffs = vertex * 3;

            vertices.add(x + face.m_Corners[cornerOffs]);
            vertices.add(y + face.m_Corners[cornerOffs + 1]);
            vertices.add(z + face.m_Corners[cornerOffs + 2]);

            vertices.add(color.x);
            vertices.add(color.y);
            vertices.add(color.z);
        }

        for(int index : FACE_INDICES)
            indices.add(firstVertex + index);
    }

    private static boolean isFaceVisible(World world, ChunkPosition chunkPosition, int localX, int localY, int localZ, Face face) {
        int chunkWorldOriginX = chunkPosition.getWorldBlockOriginX();
        int chunkWorldOriginY = chunkPosition.getWorldBlockOriginY();
        int chunkWorldOriginZ = chunkPosition.getWorldBlockOriginZ();

        int neighborWorldX = chunkWorldOriginX + localX + face.m_OffX;
        int neighborWorldY = chunkWorldOriginY + localY + face.m_OffY;
        int neighborWorldZ = chunkWorldOriginZ + localZ + face.m_OffZ;

        Block neighbor = world.getBlock(neighborWorldX, neighborWorldY, neighborWorldZ);
        return !neighbor.isOpaque();
    }

    private enum Face {

        FRONT(0, 0, 1, 0.90f, new float[]{
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f
        }),

        BACK(0, 0, -1, 0.75f, new float[] {
                1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f
        }),

        RIGHT(1, 0, 0, 0.85f, new float[] {
                1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f
        }),

        LEFT(-1, 0, 0, 0.70f, new float[] {
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f
        }),

        TOP(0, 1, 0, 1.0f, new float[] {
                0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        }),

        BOTTOM(0, -1, 0, 0.60f, new float[] {
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        });

        private final int m_OffX, m_OffY, m_OffZ;

        private final float m_Shade;
        private final float[] m_Corners;

        Face(int offX, int offY, int offZ, float shade, float[] corners) {
            m_OffX = offX;
            m_OffY = offY;
            m_OffZ = offZ;

            m_Shade = shade;
            m_Corners = corners;
        }

    }

    private static final class FloatArrayBuilder {

        private static final int INITIAL_CAPACITY = 256;

        private float[] m_Values = new float[INITIAL_CAPACITY];
        private int m_Size;

        public void add(float value) {
            ensureCapacity(m_Size + 1);

            m_Values[m_Size] = value;
            m_Size++;
        }

        public int size() {
            return m_Size;
        }

        public float[] toArray() {
            return Arrays.copyOf(m_Values, m_Size);
        }

        private void ensureCapacity(int requiredCapacity) {
            if (requiredCapacity <= m_Values.length)
                return;

            int newCapacity = Math.max(requiredCapacity, m_Values.length * 2);
            m_Values = Arrays.copyOf(m_Values, newCapacity);
        }
    }

    private static final class IntArrayBuilder {

        private static final int INITIAL_CAPACITY = 128;

        private int[] m_Values = new int[INITIAL_CAPACITY];
        private int m_Size;

        public void add(int value) {
            ensureCapacity(m_Size + 1);

            m_Values[m_Size] = value;
            m_Size++;
        }

        public int[] toArray() {
            return Arrays.copyOf(m_Values, m_Size);
        }

        private void ensureCapacity(int requiredCapacity) {
            if (requiredCapacity <= m_Values.length)
                return;

            int newCapacity = Math.max(requiredCapacity, m_Values.length * 2);
            m_Values = Arrays.copyOf(m_Values, newCapacity);
        }
    }

}
