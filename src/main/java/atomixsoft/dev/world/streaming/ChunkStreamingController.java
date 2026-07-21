package atomixsoft.dev.world.streaming;

import atomixsoft.dev.world.World;
import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;
import atomixsoft.dev.world.generation.WorldGenerator;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public final class ChunkStreamingController {

    private final World m_World;
    private final WorldGenerator m_WorldGenerator;
    private final ChunkStreamingSettings m_Settings;

    private final PriorityQueue<ChunkGenerationRequest> m_GenerationQueue;
    private final Set<ChunkPosition> m_QueuedPositions;

    private int m_CenterChunkX;
    private int m_CenterChunkZ;

    private boolean m_Initialized;

    public ChunkStreamingController(World world, WorldGenerator worldGenerator, ChunkStreamingSettings settings) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null.");

        if (worldGenerator == null)
            throw new IllegalArgumentException("World generator cannot be null.");

        if (settings == null)
            throw new IllegalArgumentException("Chunk streaming settings cannot be null.");

        m_World = world;
        m_WorldGenerator = worldGenerator;
        m_Settings = settings;

        m_GenerationQueue = new PriorityQueue<>();
        m_QueuedPositions = new HashSet<>();
    }

    private static int worldToChunkCoordinate(float worldCoordinate) {
        return (int) Math.floor(worldCoordinate / Chunk.SIZE);
    }

    private static int absoluteChunkDistance(int first, int second) {
        long difference = (long) first - second;
        return (int) Math.min(Integer.MAX_VALUE, Math.abs(difference));
    }

    public void update(float worldX, float worldZ) {
        if (!Float.isFinite(worldX) || !Float.isFinite(worldZ))
            throw new IllegalArgumentException("Streaming position must be finite.");

        int centerChunkX = worldToChunkCoordinate(worldX);

        int centerChunkZ = worldToChunkCoordinate(worldZ);

        boolean centerChanged = !m_Initialized || centerChunkX != m_CenterChunkX || centerChunkZ != m_CenterChunkZ;

        if (centerChanged) {
            m_CenterChunkX = centerChunkX;
            m_CenterChunkZ = centerChunkZ;
            m_Initialized = true;

            rebuildGenerationQueue();
            unloadDistantChunks();
        }

        processGenerationQueue();
    }

    public void prepareInitialArea(float worldX, float worldZ) {
        if (!Float.isFinite(worldX) || !Float.isFinite(worldZ))
            throw new IllegalArgumentException("Streaming position must be finite.");

        m_CenterChunkX = worldToChunkCoordinate(worldX);
        m_CenterChunkZ = worldToChunkCoordinate(worldZ);

        m_Initialized = true;

        rebuildGenerationQueue();
        unloadDistantChunks();

        while (!m_GenerationQueue.isEmpty())
            processGenerationQueue();
    }

    public void refresh() {
        if (!m_Initialized)
            throw new IllegalStateException("Chunk streaming has not been initialized.");

        rebuildGenerationQueue();
        unloadDistantChunks();
    }

    public int getCenterChunkX() {
        return m_CenterChunkX;
    }

    public int getCenterChunkZ() {
        return m_CenterChunkZ;
    }

    public ChunkStreamingSettings getSettings() {
        return m_Settings;
    }

    public int getQueuedChunkCount() {
        return m_GenerationQueue.size();
    }

    public boolean hasPendingGeneration() {
        return !m_GenerationQueue.isEmpty();
    }

    public boolean isInitialAreaReady() {
        return m_Initialized && m_GenerationQueue.isEmpty();
    }

    private void rebuildGenerationQueue() {
        m_GenerationQueue.clear();
        m_QueuedPositions.clear();

        int radius = m_Settings.loadRadius();
        for (int chunkZ = m_CenterChunkZ - radius; chunkZ <= m_CenterChunkZ + radius; chunkZ++) {
            for (int chunkX = m_CenterChunkX - radius; chunkX <= m_CenterChunkX + radius; chunkX++) {
                enqueueVerticalColumn(chunkX, chunkZ);
            }
        }
    }

    private void enqueueVerticalColumn(int chunkX, int chunkZ) {
        long horizontalPriority = calculateHorizontalPriority(chunkX, chunkZ);
        for (int chunkY = m_Settings.minChunkY(); chunkY <= m_Settings.maxChunkY(); chunkY++) {
            ChunkPosition position = new ChunkPosition(chunkX, chunkY, chunkZ);
            if (m_World.hasChunk(position))
                continue;

            if (!m_QueuedPositions.add(position))
                continue;

            long priority = calculatePriority(horizontalPriority, chunkY);
            m_GenerationQueue.add(new ChunkGenerationRequest(position, priority));
        }
    }

    private long calculateHorizontalPriority(int chunkX, int chunkZ) {
        long deltaX = (long) chunkX - m_CenterChunkX;
        long deltaZ = (long) chunkZ - m_CenterChunkZ;

        return deltaX * deltaX + deltaZ * deltaZ;
    }

    private long calculatePriority(long horizontalPriority, int chunkY) {
        long verticalOffset = (long) chunkY - m_Settings.minChunkY();
        long verticalLayerCount = (long) m_Settings.maxChunkY() - m_Settings.minChunkY() + 1L;

        return horizontalPriority * verticalLayerCount + verticalOffset;
    }

    private void processGenerationQueue() {
        int remainingBudget = m_Settings.chunksGeneratedPerFrame();
        while (remainingBudget > 0 && !m_GenerationQueue.isEmpty()) {
            ChunkGenerationRequest request = m_GenerationQueue.poll();
            ChunkPosition position = request.position();

            m_QueuedPositions.remove(position);
            if (!isInsideLoadRadius(position))
                continue;

            if (m_World.hasChunk(position))
                continue;

            m_WorldGenerator.generateChunk(m_World, position);
            remainingBudget--;
        }
    }

    private boolean isInsideLoadRadius(ChunkPosition position) {
        int radius = m_Settings.loadRadius();
        int distanceX = absoluteChunkDistance(position.x(), m_CenterChunkX);
        int distanceZ = absoluteChunkDistance(position.z(), m_CenterChunkZ);

        return distanceX <= radius && distanceZ <= radius && position.y() >= m_Settings.minChunkY() && position.y() <= m_Settings.maxChunkY();
    }

    private void unloadDistantChunks() {
        Set<ChunkPosition> loadedPositions = m_World.getChunkPositions();
        int unloadRadius = m_Settings.unloadRadius();

        for (ChunkPosition position : loadedPositions) {
            int distanceX = absoluteChunkDistance(position.x(), m_CenterChunkX);
            int distanceZ = absoluteChunkDistance(position.z(), m_CenterChunkZ);

            if (distanceX > unloadRadius || distanceZ > unloadRadius)
                m_World.removeChunk(position);
        }
    }

}