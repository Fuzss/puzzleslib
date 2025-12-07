package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

/**
 * Adds listeners to the server (data packs) resource manager to reload together with other resources.
 */
public interface DataPackReloadListenersContext {
    /**
     * The {@link RecipeManager} reload listener.
     */
    ResourceLocation RECIPES = ResourceLocation.withDefaultNamespace("recipes");
    /**
     * The {@link ServerFunctionLibrary} reload listener.
     */
    ResourceLocation FUNCTIONS = ResourceLocation.withDefaultNamespace("functions");
    /**
     * The {@link ServerAdvancementManager} reload listener.
     */
    ResourceLocation ADVANCEMENTS = ResourceLocation.withDefaultNamespace("advancements");

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location
     * @param reloadListenerFactory the reload listener factory
     */
    void registerReloadListener(ResourceLocation resourceLocation, PreparableReloadListenerFactory reloadListenerFactory);

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, PreparableReloadListenerFactory reloadListenerFactory);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location
     * @param reloadListenerFactory the reload listener factory
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceManagerReloadListenerFactory reloadListenerFactory) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, ResourceManagerReloadListenerFactory reloadListenerFactory) {
        this.registerReloadListener(resourceLocation,
                otherResourceLocation,
                (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, SimplePreparableReloadListenerFactory<T> reloadListenerFactory) {
        this.registerReloadListener(resourceLocation, (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param resourceLocation      the reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param otherResourceLocation the other reload listener resource location, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default <T> void registerReloadListener(ResourceLocation resourceLocation, ResourceLocation otherResourceLocation, SimplePreparableReloadListenerFactory<T> reloadListenerFactory) {
        this.registerReloadListener(resourceLocation,
                otherResourceLocation,
                (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    @FunctionalInterface
    interface PreparableReloadListenerFactory {
        PreparableReloadListener apply(ReloadableServerResources serverResources, HolderLookup.Provider lookupWithUpdatedTags);
    }

    @FunctionalInterface
    interface ResourceManagerReloadListenerFactory {
        ResourceManagerReloadListener apply(ReloadableServerResources serverResources, HolderLookup.Provider lookupWithUpdatedTags);
    }

    @FunctionalInterface
    interface SimplePreparableReloadListenerFactory<T> {
        SimplePreparableReloadListener<T> apply(ReloadableServerResources serverResources, HolderLookup.Provider lookupWithUpdatedTags);
    }
}
