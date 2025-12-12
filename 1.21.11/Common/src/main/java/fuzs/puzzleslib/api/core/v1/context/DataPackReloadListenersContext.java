package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

/**
 * Adds listeners to the server (data packs) resource manager to reload together with other resources.
 */
public interface DataPackReloadListenersContext {
    /**
     * The {@link net.minecraft.world.item.crafting.RecipeManager} reload listener.
     */
    Identifier RECIPES = Identifier.withDefaultNamespace("recipes");
    /**
     * The {@link net.minecraft.server.ServerFunctionLibrary} reload listener.
     */
    Identifier FUNCTIONS = Identifier.withDefaultNamespace("functions");
    /**
     * The {@link net.minecraft.server.ServerAdvancementManager} reload listener.
     */
    Identifier ADVANCEMENTS = Identifier.withDefaultNamespace("advancements");

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param identifier            the reload listener identifier
     * @param reloadListenerFactory the reload listener factory
     */
    void registerReloadListener(Identifier identifier, PreparableReloadListenerFactory reloadListenerFactory);

    /**
     * Register a {@link PreparableReloadListener}.
     *
     * @param identifier            the reload listener identifier
     * @param otherIdentifier       the other reload listener identifier, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    void registerReloadListener(Identifier identifier, Identifier otherIdentifier, PreparableReloadListenerFactory reloadListenerFactory);

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param identifier            the reload listener identifier
     * @param reloadListenerFactory the reload listener factory
     */
    default void registerReloadListener(Identifier identifier, ResourceManagerReloadListenerFactory reloadListenerFactory) {
        this.registerReloadListener(identifier, (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link ResourceManagerReloadListener}.
     *
     * @param identifier            the reload listener identifier, either for the new listener or for the existing
     *                              vanilla listener
     * @param otherIdentifier       the other reload listener identifier, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default void registerReloadListener(Identifier identifier, Identifier otherIdentifier, ResourceManagerReloadListenerFactory reloadListenerFactory) {
        this.registerReloadListener(identifier,
                otherIdentifier,
                (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param identifier            the reload listener identifier, either for the new listener or for the existing
     *                              vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default <T> void registerReloadListener(Identifier identifier, SimplePreparableReloadListenerFactory<T> reloadListenerFactory) {
        this.registerReloadListener(identifier, (PreparableReloadListenerFactory) reloadListenerFactory::apply);
    }

    /**
     * Register a {@link SimplePreparableReloadListener}.
     *
     * @param identifier            the reload listener identifier, either for the new listener or for the existing
     *                              vanilla listener
     * @param otherIdentifier       the other reload listener identifier, either for the new listener or for the
     *                              existing vanilla listener
     * @param reloadListenerFactory the reload listener factory
     */
    default <T> void registerReloadListener(Identifier identifier, Identifier otherIdentifier, SimplePreparableReloadListenerFactory<T> reloadListenerFactory) {
        this.registerReloadListener(identifier,
                otherIdentifier,
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
