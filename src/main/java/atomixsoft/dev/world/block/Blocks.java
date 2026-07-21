package atomixsoft.dev.world.block;

import org.joml.Vector3f;

public final class Blocks {

    public static Block AIR, GRASS, DIRT, STONE;

    private static boolean s_Initialized;

    private Blocks() {}

    public static void Initialize() {
        if(s_Initialized)
            throw new IllegalStateException("Blocks have already been initialized!");

        AIR   = BlockRegistry.Register("Air", false, false, new Vector3f(0.0f, 0.0f, 0.0f));
        GRASS = BlockRegistry.Register("Grass", true, true, new Vector3f(0.25f, 0.70f, 0.20f));
        DIRT  = BlockRegistry.Register("Dirt", true, true, new Vector3f(0.45f, 0.28f, 0.12f));
        STONE = BlockRegistry.Register("Stone", true, true, new Vector3f(0.55f));

        BlockRegistry.Freeze();
        s_Initialized = true;
    }

    public static void Dispose() {
        AIR = null;
        GRASS = null;
        DIRT = null;
        STONE = null;

        BlockRegistry.Clear();
        s_Initialized = false;
    }

    public static boolean IsInitialized() {
        return s_Initialized;
    }

}
