package atomixsoft.dev.world.generation.shape;

@FunctionalInterface
public interface TerrainShape {

    int calculateSurfaceHeight(int worldX, int worldZ);

}
