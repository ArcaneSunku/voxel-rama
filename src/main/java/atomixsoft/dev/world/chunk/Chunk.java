package atomixsoft.dev.world.chunk;

import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.block.BlockRegistry;
import atomixsoft.dev.world.block.Blocks;

import java.util.Arrays;

public final class Chunk {

    public static final int SIZE = 16;
    public static final int BLOCK_COUNTER = SIZE * SIZE * SIZE;

    private final short[] m_BlockIds;
    private boolean m_Dirty;

    public Chunk() {
        if(!Blocks.IsInitialized())
            throw new IllegalStateException("Blocks must be initialized before Chunk Creation!");

        m_BlockIds = new short[BLOCK_COUNTER];
        m_Dirty = true;
    }

    public void fill(Block block) {
        if(block == null)
            throw new IllegalArgumentException("Block cannot be null!");

        fill(block.getId());
    }

    public void fill(short blockId) {
        BlockRegistry.Get(blockId);
        boolean changed = false;

        for(short currBlockId : m_BlockIds) {
            if(currBlockId != blockId) {
                changed = true;
                break;
            }
        }

        if(!changed)
            return;

        Arrays.fill(m_BlockIds, blockId);
        m_Dirty = true;
    }

    public void setBlock(int x, int y, int z, Block block) {
        if(block == null)
            throw new IllegalArgumentException("Block cannot be null!");

        setBlockId(x, y, z, block.getId());
    }

    public void setBlockId(int x, int y, int z, short id) {
        validateCoords(x, y, z);
        BlockRegistry.Get(id);

        int index = getIndex(x, y, z);
        if(m_BlockIds[index] == id)
            return;

        m_BlockIds[index] = id;
        m_Dirty = true;
    }

    public void markMesh(boolean dirty) {
        m_Dirty = dirty;
    }

    public void clean() {
        markMesh(false);
    }

    public Block getBlock(int x, int y, int z) {
        return BlockRegistry.Get(getBlockId(x, y, z));
    }

    public short getBlockId(int x, int y, int z) {
        validateCoords(x, y, z);
        return m_BlockIds[getIndex(x, y, z)];
    }

    public boolean isInBounds(int x, int y, int z) {
        return x >= 0 && x < SIZE &&
                y >= 0 && y < SIZE &&
                z >= 0 && z < SIZE;
    }

    public boolean isDirty() {
        return m_Dirty;
    }

    private void validateCoords(int x, int y, int z) {
        if(!isInBounds(x, y, z))
            throw new IndexOutOfBoundsException(String.format("Block Coords are outside the chunk: (%d, %d, %d)", x, y, z));
    }

    private int getIndex(int x, int y, int z) {
        return x + z * SIZE + y * SIZE * SIZE;
    }

}
