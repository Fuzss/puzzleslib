package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import net.minecraft.client.searchtree.SearchRegistry;

import java.util.Objects;

/**
 * Register a search tree to {@link SearchRegistry}.
 *
 * @deprecated replaced with direct access to the search registry via {@link ClientAbstractions#getSearchRegistry()}
 */
@Deprecated(forRemoval = true)
public interface SearchRegistryContext {

    /**
     * Register a search tree to {@link SearchRegistry}.
     *
     * @param searchRegistryKey the search tree token
     * @param treeBuilder       builder supplier for search tree
     * @param <T>               type to be searched for
     */
    default <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, SearchRegistry.TreeBuilderSupplier<T> treeBuilder) {
        Objects.requireNonNull(searchRegistryKey, "search registry key is null");
        Objects.requireNonNull(treeBuilder, "tree builder is null");
        SearchRegistry searchRegistry = ClientAbstractions.INSTANCE.getSearchRegistry();
        Objects.requireNonNull(searchRegistry, "search tree manager is null");
        searchRegistry.register(searchRegistryKey, treeBuilder);
    }
}
