package atomixsoft.dev.world.gen.shape;

@FunctionalInterface
public interface TerrainShape {

    int calculateSurfaceHeight(int worldX, int worldZ);

}
