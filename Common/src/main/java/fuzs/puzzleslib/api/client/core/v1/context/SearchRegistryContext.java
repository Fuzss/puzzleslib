package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.searchtree.SearchRegistry;

/**
 * register search tree to private registry in Minecraft singleton
 */
@FunctionalInterface
public interface SearchRegistryContext {

    /**
     * registers a search tree to {@link SearchRegistry} in {@link net.minecraft.world.entity.vehicle.Minecart}
     *
     * @param searchRegistryKey the search tree token
     * @param treeBuilder       builder supplier for search tree
     * @param <T>               type to be searched for
     */
    <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder);
}
