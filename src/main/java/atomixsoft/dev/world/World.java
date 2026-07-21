package atomixsoft.dev.world;

import atomixsoft.dev.world.block.Block;
import atomixsoft.dev.world.block.BlockRegistry;
import atomixsoft.dev.world.block.Blocks;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

import java.util.*;

public class World {

    private final Map<ChunkPosition, Chunk> m_Chunks;
    private final WorldProperties m_WorldProperties;

    public World(WorldProperties properties) {
        if(!Blocks.IsInitialized())
            throw new IllegalStateException("Blocks must be initialized before creating a World.");

        m_Chunks = new HashMap<>();
        m_WorldProperties = properties;
    }

    public Chunk createChunk(int x, int y, int z) {
        return createChunk(new ChunkPosition(x, y, z));
    }

    public Chunk createChunk(ChunkPosition position) {
        if(position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        if(m_Chunks.containsKey(position))
            throw new IllegalStateException("Chunk already exists @ " + position);

        Chunk chunk = new Chunk();
        m_Chunks.put(position, chunk);

        markNeighborMeshesDirty(position);
        return chunk;
    }

    public void clear() {
        m_Chunks.clear();
    }

    public WorldProperties getProperties() {
        return m_WorldProperties;
    }

    public long getSeed() {
        return m_WorldProperties.seedValue();
    }

    public static ChunkPosition getChunkPosition(int worldX, int worldY, int worldZ) {
        return new ChunkPosition(Math.floorDiv(worldX, Chunk.SIZE), Math.floorDiv(worldY, Chunk.SIZE), Math.floorDiv(worldZ, Chunk.SIZE));
    }

    public void addChunk(ChunkPosition position, Chunk chunk) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        if (chunk == null)
            throw new IllegalArgumentException("Chunk cannot be null.");

        if (m_Chunks.containsKey(position))
            throw new IllegalStateException("A chunk already exists at " + position + ".");

        m_Chunks.put(position, chunk);

        chunk.markMesh(true);
        markNeighborMeshesDirty(position);
    }

    public boolean removeChunk(int chunkX, int chunkY, int chunkZ) {
        return removeChunk(new ChunkPosition(chunkX, chunkY, chunkZ));
    }

    public boolean removeChunk(ChunkPosition position) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        Chunk removedChunk = m_Chunks.remove(position);
        if (removedChunk == null)
            return false;

        markNeighborMeshesDirty(position);
        return true;
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return getChunk(new ChunkPosition(chunkX, chunkY, chunkZ));
    }

    public Chunk getChunk(ChunkPosition position) {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        return m_Chunks.get(position);
    }

    public Chunk requireChunk(int chunkX, int chunkY, int chunkZ) {
        return requireChunk(new ChunkPosition(chunkX, chunkY, chunkZ));
    }

    public Chunk requireChunk(ChunkPosition position) {
        Chunk chunk = getChunk(position);
        if (chunk == null)
            throw new IllegalArgumentException("No chunk exists at " + position + ".");

        return chunk;
    }

    public boolean hasChunk(int chunkX, int chunkY, int chunkZ) {
        return hasChunk(new ChunkPosition(chunkX, chunkY, chunkZ));
    }

    public boolean hasChunk(ChunkPosition position) {
        if (position == null)
            return false;

        return m_Chunks.containsKey(position);
    }

    public int getChunkCount() {
        return m_Chunks.size();
    }

    public Map<ChunkPosition, Chunk> getChunks() {
        return Collections.unmodifiableMap(m_Chunks);
    }

    public Set<ChunkPosition> getChunkPositions() {
        return Set.copyOf(m_Chunks.keySet());
    }

    public Collection<Chunk> getChunkValues() {
        return Collections.unmodifiableCollection(m_Chunks.values());
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        return BlockRegistry.Get(getBlockId(worldX, worldY, worldZ));
    }

    public short getBlockId(int worldX, int worldY, int worldZ) {
        ChunkPosition chunkPosition = getChunkPosition(worldX, worldY, worldZ);
        Chunk chunk = m_Chunks.get(chunkPosition);

        if (chunk == null)
            return Blocks.AIR.getId();

        int localX = Math.floorMod(worldX, Chunk.SIZE);
        int localY = Math.floorMod(worldY, Chunk.SIZE);
        int localZ = Math.floorMod(worldZ, Chunk.SIZE);

        return chunk.getBlockId(localX, localY, localZ);
    }

    public void setBlock(int worldX, int worldY, int worldZ, Block block) {
        if (block == null)
            throw new IllegalArgumentException("Block cannot be null.");

        setBlockId(worldX, worldY, worldZ, block.getId());
    }

    public void setBlockId(int worldX, int worldY, int worldZ, short blockId) {
        BlockRegistry.Get(blockId);

        ChunkPosition chunkPosition = getChunkPosition(worldX, worldY, worldZ);
        Chunk chunk = requireChunk(chunkPosition);

        int localX = Math.floorMod(worldX, Chunk.SIZE);
        int localY = Math.floorMod(worldY, Chunk.SIZE);
        int localZ = Math.floorMod(worldZ, Chunk.SIZE);

        short previousBlockId = chunk.getBlockId(localX, localY, localZ);
        if (previousBlockId == blockId)
            return;

        chunk.setBlockId(localX, localY, localZ, blockId);
        markBoundaryNeighborsDirty(chunkPosition, localX, localY, localZ);
    }

    private void markBoundaryNeighborsDirty(ChunkPosition position, int localX, int localY, int localZ) {
        if(localX == 0)
            markMeshDirty(position.offset(-1, 0, 0));
        else if(localX == Chunk.SIZE - 1)
            markMeshDirty(position.offset(1, 0, 0));

        if(localY == 0)
            markMeshDirty(position.offset(0, -1, 0));
        else if(localY == Chunk.SIZE - 1)
            markMeshDirty(position.offset(0, 1, 0));

        if(localZ == 0)
            markMeshDirty(position.offset(0, 0, -1));
        else if(localZ == Chunk.SIZE - 1)
            markMeshDirty(position.offset(0, 0, 1));
    }

    private void markNeighborMeshesDirty(ChunkPosition position) {
        markMeshDirty(position.offset(-1, 0, 0));
        markMeshDirty(position.offset(1, 0, 0));

        markMeshDirty(position.offset(0, -1, 0));
        markMeshDirty(position.offset(0, 1, 0));

        markMeshDirty(position.offset(0, 0, -1));
        markMeshDirty(position.offset(0, 0, 1));
    }

    private void markMeshDirty(ChunkPosition position) {
        Chunk chunk = m_Chunks.get(position);
        if(chunk != null)
            chunk.markMesh(true);
    }

}
