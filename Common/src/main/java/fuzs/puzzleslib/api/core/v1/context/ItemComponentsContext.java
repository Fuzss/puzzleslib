package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Predicate;

/**
 * Register patches for item data components.
 */
public interface ItemComponentsContext {

    /**
     * @param item        the item
     * @param initializer apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Item item, Initializer<Item> initializer);

    /**
     * @param itemPredicate the item filter
     * @param initializer   apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer<Item> initializer);

    /**
     * @param <T> the value type
     * @see net.minecraft.core.component.DataComponentInitializers.Initializer
     */
    @FunctionalInterface
    interface Initializer<T> {
        /**
         * @param components the access for getting existing values
         * @param builder    the builder for setting new values
         * @param context    the holder lookup
         * @param key        the resource key
         */
        void run(DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider context, ResourceKey<T> key);
    }
}
