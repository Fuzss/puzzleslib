package fuzs.puzzleslib.api.client.core.v1.contexts;

/**
 * Context for registering various listeners that run right after baked models have been reloaded.
 */
@FunctionalInterface
public interface ModelBakingListenersContext {

    /**
     * Register a reload listener to run every time when models are reloaded.
     *
     * @param modelBakingContext action that runs everytime baked models are reloaded
     */
    <T extends DynamicModelBakingContext> void registerReloadListener(T modelBakingContext);
}
