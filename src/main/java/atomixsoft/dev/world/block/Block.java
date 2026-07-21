package atomixsoft.dev.world.block;

import org.joml.Vector3f;

public final class Block {

    private final short m_Id;
    private final String m_Name;

    private final boolean m_Solid;
    private final boolean m_Opaque;

    private final Vector3f m_Color;

    Block(short id, String name, boolean solid, boolean opaque, Vector3f color) {
        if(id < 0)
            throw new IllegalArgumentException("Block ID cannot be negative.");

        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Block name cannot be null or blank.");

        ValidateColor(color);

        m_Id = id;
        m_Name = name;

        m_Solid = solid;
        m_Opaque = opaque;

        m_Color = color;
    }

    private static void ValidateColor(Vector3f color) {
        if(color.x < 0.0f || color.x > 1.0f)
            throw new IllegalArgumentException("Color component Red must be between 0 and 1!");

        if(color.y < 0.0f || color.y > 1.0f)
            throw new IllegalArgumentException("Color component Green must be between 0 and 1!");

        if(color.z < 0.0f || color.z > 1.0f)
            throw new IllegalArgumentException("Color component Blue must be between 0 and 1!");
    }

    public short getId() {
        return m_Id;
    }

    public String getName() {
        return m_Name;
    }

    public boolean isSolid() {
        return m_Solid;
    }

    public boolean isOpaque() {
        return m_Opaque;
    }

    public Vector3f getColor() {
        return m_Color;
    }

    @Override
    public String toString() {
        return String.format("Block{id=%s, name=%s, solid=%b, opaque=%b, color[r=%f, g=%f, b=%f]}", m_Id, m_Name, m_Solid, m_Opaque);
    }

}
