package atomixsoft.dev.world.biome;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class Biomes {

    public static final BiomeDefinition OCEAN  = new BiomeDefinition(BiomeId.OCEAN, "Ocean", 0.50f, 0.55f, 0.08f, 0.50f, 0.35f, 4.00f);
    public static final BiomeDefinition PLAINS = new BiomeDefinition(BiomeId.PLAINS, "Plains", 0.58f, 0.48f, 0.62f, 1.25f, 1.00f, 1.25f);
    public static final BiomeDefinition FOREST = new BiomeDefinition(BiomeId.FOREST, "Forest", 0.55f, 0.78f, 0.62f, 1.25f, 1.75f, 1.00f);
    public static final BiomeDefinition DESERT = new BiomeDefinition(BiomeId.DESERT, "Desert", 0.92f, 0.12f, 0.68f, 2.00f, 2.25f, 1.00f);
    public static final BiomeDefinition TAIGA  = new BiomeDefinition(BiomeId.TAIGA, "Taiga", 0.28f, 0.68f, 0.60f, 1.75f, 1.25f, 1.00f);
    public static final BiomeDefinition TUNDRA = new BiomeDefinition(BiomeId.TUNDRA, "Tundra", 0.08f, 0.30f, 0.58f, 2.50f, 0.75f, 1.00f);

    private static final Map<BiomeId, BiomeDefinition> DEFINITIONS;
    private static final Collection<BiomeDefinition> VALUES;

    static {
        EnumMap<BiomeId, BiomeDefinition> definitions = new EnumMap<>(BiomeId.class);

        register(definitions, OCEAN);
        register(definitions, PLAINS);
        register(definitions, FOREST);
        register(definitions, DESERT);
        register(definitions, TAIGA);
        register(definitions, TUNDRA);

        DEFINITIONS = Collections.unmodifiableMap(definitions);
        VALUES = Collections.unmodifiableCollection(definitions.values());
    }

    private Biomes() {
    }

    public static BiomeDefinition get(BiomeId id) {
        if (id == null)
            throw new IllegalArgumentException("Biome ID cannot be null.");

        BiomeDefinition definition = DEFINITIONS.get(id);
        if (definition == null)
            throw new IllegalArgumentException("Unknown biome ID: " + id);

        return definition;
    }

    public static Collection<BiomeDefinition> values() {
        return VALUES;
    }

    private static void register(EnumMap<BiomeId, BiomeDefinition> definitions, BiomeDefinition definition) {
        BiomeDefinition previous = definitions.put(definition.id(), definition);
        if (previous != null)
            throw new IllegalStateException("Duplicate biome ID: " + definition.id());
    }
}