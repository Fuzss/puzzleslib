package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SearchRegistryContext;
import fuzs.puzzleslib.mixin.client.accessor.MinecraftFabricAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;

import java.util.Objects;

public final class SearchRegistryContextFabricImpl implements SearchRegistryContext {

    @Override
    public <T> void registerSearchTree(SearchRegistry.Key<T> searchRegistryKey, MutableSearchTree<T> treeBuilder) {
        Objects.requireNonNull(searchRegistryKey, "search registry key is null");
        Objects.requireNonNull(treeBuilder, "search registry tree builder is null");
        SearchRegistry searchTreeManager = ((MinecraftFabricAccessor) Minecraft.getInstance()).puzzleslib$getSearchRegistry();
        Objects.requireNonNull(searchTreeManager, "search tree manager is null");
        searchTreeManager.register(searchRegistryKey, treeBuilder);
    }
}
