package atomixsoft.dev.world.streaming;

import atomixsoft.dev.world.chunk.Chunk;
import atomixsoft.dev.world.chunk.ChunkPosition;

public record ChunkGenerationResult(ChunkPosition position, Chunk chunk, Throwable failure) {

    public ChunkGenerationResult {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");

        boolean hasChunk = chunk != null;
        boolean hasFailure = failure != null;

        if (hasChunk == hasFailure)
            throw new IllegalArgumentException("A generation result must contain either a chunk or a failure.");
    }

    public static ChunkGenerationResult succeeded(ChunkPosition position, Chunk chunk) {
        if (chunk == null)
            throw new IllegalArgumentException("Generated chunk cannot be null.");

        return new ChunkGenerationResult(position, chunk, null);
    }

    public static ChunkGenerationResult failed(ChunkPosition position, Throwable failure) {
        if (failure == null)
            throw new IllegalArgumentException("Generation failure cannot be null.");

        return new ChunkGenerationResult(position, null, failure);
    }

    public boolean wasSuccessful() {
        return chunk != null;
    }

}
