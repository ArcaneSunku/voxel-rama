package atomixsoft.dev.world.chunk;

public record ChunkPosition(int x, int y, int z) {

    public ChunkPosition offset(int xOff, int yOff, int zOff) {
        return new ChunkPosition(x + xOff, y + yOff, z + zOff);
    }

    public int getWorldBlockOriginX() {
        return x * Chunk.SIZE;
    }

    public int getWorldBlockOriginY() {
        return y * Chunk.SIZE;
    }

    public int getWorldBlockOriginZ() {
        return z * Chunk.SIZE;
    }

}
