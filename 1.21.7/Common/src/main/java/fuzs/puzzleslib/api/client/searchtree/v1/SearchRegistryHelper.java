package fuzs.puzzleslib.api.client.searchtree.v1;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A helper for dealing with {@link SearchTree}.
 */
public final class SearchRegistryHelper {
    private static final Map<SearchTreeType<?>, Entry<?>> SEARCH_TREES = new IdentityHashMap<>();

    private SearchRegistryHelper() {
        // NO-OP
    }

    /**
     * Register a search tree factory for a type.
     *
     * @param type    search tree type token
     * @param factory tree factory for recreating when populating values
     * @param <T>     search item type
     */
    public static <T> void register(SearchTreeType<T> type, Function<List<T>, SearchTree<T>> factory) {
        SEARCH_TREES.put(type, new Entry<>(factory));
    }

    /**
     * Collects all tooltip lines from an item stack.
     *
     * @param itemStack the item stack
     * @return all tooltip lines from the item stack
     */
    public static Stream<String> getTooltipLines(ItemStack itemStack) {
        return getTooltipLines(Stream.of(itemStack), TooltipFlag.NORMAL);
    }

    /**
     * Collects all tooltip lines from item stacks.
     *
     * @param stream      the item stacks
     * @param tooltipFlag the tooltip flag
     * @return all tooltip lines from the item stacks
     */
    public static Stream<String> getTooltipLines(Stream<ItemStack> stream, TooltipFlag tooltipFlag) {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        return SessionSearchTrees.getTooltipLines(stream,
                Item.TooltipContext.of(clientPacketListener.registryAccess()),
                tooltipFlag);
    }

    /**
     * Populates a search tree with new values.
     *
     * @param type   search tree type token
     * @param values value for populating the tree
     * @param <T>    search item type
     */
    public static <T> void populateSearchTree(SearchTreeType<T> type, List<T> values) {
        Entry<T> entry = lookupEntry(type);
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(clientPacketListener, "client packet listener is null");
        clientPacketListener.searchTrees().register(entry.key, () -> {
            CompletableFuture<SearchTree<T>> searchTree = entry.searchTree;
            entry.searchTree = CompletableFuture.supplyAsync(() -> {
                return entry.factory.apply(values);
            }, Util.backgroundExecutor());
            searchTree.cancel(true);
        });
    }

    /**
     * Get the current search tree for a type.
     *
     * @param type search tree type token
     * @param <T>  search item type
     * @return the search tree
     */
    public static <T> SearchTree<T> getSearchTree(SearchTreeType<T> type) {
        return lookupEntry(type).searchTree.join();
    }

    private static <T> Entry<T> lookupEntry(SearchTreeType<T> type) {
        Entry<?> entry = SEARCH_TREES.get(type);
        Objects.requireNonNull(entry, () -> "Search tree type " + type.resourceLocation() + " is not registered");
        return (Entry<T>) entry;
    }

    private static class Entry<T> {
        public final SessionSearchTrees.Key key = new SessionSearchTrees.Key();
        public final Function<List<T>, SearchTree<T>> factory;
        public CompletableFuture<SearchTree<T>> searchTree = CompletableFuture.completedFuture(SearchTree.empty());

        Entry(Function<List<T>, SearchTree<T>> factory) {
            this.factory = factory;
        }
    }
}
