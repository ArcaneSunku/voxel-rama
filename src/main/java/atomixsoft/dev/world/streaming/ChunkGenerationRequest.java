package atomixsoft.dev.world.streaming;

import atomixsoft.dev.world.chunk.ChunkPosition;

public record ChunkGenerationRequest(ChunkPosition position,
                                     long priority) implements Comparable<ChunkGenerationRequest> {

    public ChunkGenerationRequest {
        if (position == null)
            throw new IllegalArgumentException("Chunk position cannot be null.");
    }

    @Override
    public int compareTo(ChunkGenerationRequest other) {
        int priorityComparison = Long.compare(priority, other.priority);
        if (priorityComparison != 0)
            return priorityComparison;

        int yComparison = Integer.compare(position.y(), other.position().y());
        if (yComparison != 0)
            return yComparison;

        int zComparison = Integer.compare(position.z(), other.position().z());
        if (zComparison != 0)
            return zComparison;

        return Integer.compare(position.x(), other.position().x());
    }

}