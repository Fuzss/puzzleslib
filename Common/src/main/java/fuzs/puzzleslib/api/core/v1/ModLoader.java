package fuzs.puzzleslib.api.core.v1;

/**
 * types of mod loaders mods are built for
 */
public enum ModLoader {
    /**
     * the Fabric loader
     */
    FABRIC,
    /**
     * the NeoForge loader
     */
    NEOFORGE,
    /**
     * the Forge loader
     */
    FORGE,
    /**
     * the Quilt loader
     */
    QUILT;

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
