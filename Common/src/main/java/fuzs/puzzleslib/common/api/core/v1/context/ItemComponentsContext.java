package fuzs.puzzleslib.common.api.core.v1.context;

import com.google.common.base.Predicates;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
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
    void registerItemComponentsPatch(Item item, Initializer initializer);

    /**
     * @param itemPredicate the item filter
     * @param initializer   apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer initializer);

    /**
     * @param initializer apply changes to the data component map builder before it is finalized
     */
    default void registerItemComponentsPatch(Initializer initializer) {
        this.registerItemComponentsPatch(Predicates.alwaysTrue(), initializer);
    }

    /**
     * @see net.minecraft.core.component.DataComponentInitializers.Initializer
     */
    @FunctionalInterface
    interface Initializer {
        /**
         * @param components the access for getting existing values
         * @param builder    the builder for setting new values
         * @param registries the holder lookup
         * @param item       the item
         */
        void run(DataComponentGetter components, DataComponentMap.Builder builder, HolderLookup.Provider registries, Item item);
    }
}
