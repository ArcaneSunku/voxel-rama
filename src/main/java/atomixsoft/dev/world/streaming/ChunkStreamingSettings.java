package atomixsoft.dev.world.streaming;

public record ChunkStreamingSettings(int loadRadius, int unloadRadius, int minChunkY, int maxChunkY, int chunksGeneratedPerFrame) {

    public ChunkStreamingSettings {
        if (loadRadius < 0)
            throw new IllegalArgumentException("Chunk load radius cannot be negative.");

        if (unloadRadius < loadRadius)
            throw new IllegalArgumentException("Chunk unload radius cannot be smaller than the load radius.");

        if (minChunkY > maxChunkY)
            throw new IllegalArgumentException("Minimum chunk Y cannot exceed maximum chunk Y.");

        if (chunksGeneratedPerFrame < 1)
            throw new IllegalArgumentException("Chunks generated per frame must be at least one.");
    }

    public int loadedDiameter() {
        return loadRadius * 2 + 1;
    }

    public int maximumLoadedChunkCount() {
        int diameter = unloadRadius * 2 + 1;
        int verticalLayers = maxChunkY - minChunkY + 1;

        return diameter * diameter * verticalLayers;
    }

}
