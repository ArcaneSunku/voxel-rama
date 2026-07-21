package atomixsoft.dev.world.block;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class BlockRegistry {

    private static final int MAX_BLOCK_COUNT = Short.MAX_VALUE + 1;

    private static final List<Block> s_Blocks = new ArrayList<>();

    private static boolean s_Frozen;

    private BlockRegistry() {}

    public static Block Register(String name, boolean solid, boolean opaque, Vector3f color) {
        if(s_Frozen)
            throw new IllegalStateException("Cannot register blocks after the registry is frozen!");

        if(s_Blocks.size() >= MAX_BLOCK_COUNT)
            throw new IllegalStateException("The block registry has reached capacity!");

        short id = (short) s_Blocks.size();
        Block block = new Block(id, name, solid, opaque, color);

        s_Blocks.add(block);
        return block;
    }

    public static Block Get(short id) {
        int index = Short.toUnsignedInt(id);
        if(index >= s_Blocks.size())
            throw new IllegalArgumentException("Unknown block ID " + id);

        return s_Blocks.get(index);
    }

    public static Block Get(int id) {
        if(id < 0 || id >= s_Blocks.size())
            throw new IllegalArgumentException("Unknown block ID " + id);

        return s_Blocks.get(id);
    }

    public static void Freeze() {
        if(s_Blocks.isEmpty())
            throw new IllegalStateException("Cannot freeze Block Registry if it is empty!");

        s_Frozen = true;
    }

    public static void Clear() {
        s_Blocks.clear();
        s_Frozen = false;
    }

    public static List<Block> GetBlocks() {
        return s_Blocks;
    }

    public static int Size() {
        return s_Blocks.size();
    }

    public boolean isFrozen() {
        return s_Frozen;
    }
}
