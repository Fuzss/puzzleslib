package fuzs.puzzleslib.api.core.v1.contexts;

/**
 * enqueue work to be run sequentially for all mods as the construct/setup phase runs in parallel on Forge
 */
@FunctionalInterface
public interface ModLifecycleContext {

    /**
     * enqueue work to be run sequentially for all mods as the construct/setup phase runs in parallel on Forge
     *
     * @param runnable the work
     */
    void enqueueWork(Runnable runnable);
}
