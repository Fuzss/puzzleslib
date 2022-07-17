package fuzs.puzzleslib.core;

/**
 * types of mod loaders our mods are built for
 */
public enum ModLoader {
    /**
     * the Forge loader
     */
    FORGE,
    /**
     * the Fabric loader
     */
    FABRIC,
    /**
     * the Quilt loader
     */
    QUILT;

    /**
     * @return is this Forge
     */
    public boolean isForge() {
        return this == FORGE;
    }

    /**
     * @return is this Fabric
     */
    public boolean isFabric() {
        return this == FABRIC;
    }

    /**
     * @return is this Quilt
     */
    public boolean isQuilt() {
        return this == QUILT;
    }
}
