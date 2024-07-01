package fuzs.puzzleslib.api.core.v1;

import java.util.stream.Stream;

/**
 * Types of supported mod loaders.
 */
public enum ModLoader {
    /**
     * The Fabric loader.
     */
    FABRIC,
    /**
     * The NeoForge loader.
     */
    NEOFORGE,
    /**
     * The Forge loader.
     */
    FORGE,
    /**
     * The Quilt loader.
     */
    QUILT;

    /**
     * @return array containing Fabric and Quilt
     */
    public static ModLoader[] getFabricLike() {
        return Stream.of(ModLoader.values()).filter(ModLoader::isFabricLike).toArray(ModLoader[]::new);
    }

    /**
     * @return array containing NeoForge and Forge
     */
    public static ModLoader[] getForgeLike() {
        return Stream.of(ModLoader.values()).filter(ModLoader::isForgeLike).toArray(ModLoader[]::new);
    }

    /**
     * @return is this Fabric
     */
    public boolean isFabric() {
        return this == FABRIC;
    }

    /**
     * @return is this NeoForge
     */
    public boolean isNeoForge() {
        return this == NEOFORGE;
    }

    /**
     * @return is this Forge
     */
    public boolean isForge() {
        return this == FORGE;
    }

    /**
     * @return is this Quilt
     */
    public boolean isQuilt() {
        return this == QUILT;
    }

    /**
     * @return is this Fabric or Quilt
     */
    public boolean isFabricLike() {
        return this.isFabric() || this.isQuilt();
    }

    /**
     * @return is this NeoForge or Forge
     */
    public boolean isForgeLike() {
        return this.isNeoForge() || this.isForge();
    }
}
