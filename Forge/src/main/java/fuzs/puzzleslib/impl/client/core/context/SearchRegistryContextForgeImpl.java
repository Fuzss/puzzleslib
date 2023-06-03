package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SearchRegistryContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;

import java.util.Objects;

public final class SearchRegistryContextForgeImpl implements SearchRegistryContext {

    @Override
    public <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, MutableSearchTree<T> treeBuilder) {
        Objects.requireNonNull(searchRegistryKey, "search registry key is null");
        Objects.requireNonNull(treeBuilder, "tree builder is null");
        SearchRegistry searchTreeManager = Minecraft.getInstance().getSearchTreeManager();
        Objects.requireNonNull(searchTreeManager, "search tree manager is null");
        searchTreeManager.register(searchRegistryKey, treeBuilder);
    }
}
