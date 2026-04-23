package fuzs.puzzleslib.api.core.v1.context;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Register patches for item data components.
 */
public interface ItemComponentsContext {

    /**
     * @param item     the item
     * @param consumer apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Item item, BiConsumer<Builder, HolderLookup.Provider> consumer);

    /**
     * @param itemPredicate the item filter
     * @param consumer      apply changes to the data component map builder before it is finalized
     */
    void registerItemComponentsPatch(Predicate<Item> itemPredicate, BiConsumer<Builder, HolderLookup.Provider> consumer);

    /**
     * Basically {@link net.minecraft.core.component.DataComponentGetter}, but with redundant methods removed.
     * <p>
     * A custom implementation is necessary to avoid type parameters clashing in
     * {@link net.minecraft.core.component.DataComponentMap.Builder} from an injected interface on Fabric.
     */
    interface DataComponentMapGetter {
        /**
         * @param type the data component type
         * @param <T>  the value type
         * @return the value or null
         *
         * @see net.minecraft.core.component.DataComponentGetter#get(DataComponentType)
         */
        <T> @Nullable T get(DataComponentType<? extends T> type);

        /**
         * Note that this cannot be named {@code getOrDefault} as it will clash with an injected method on Fabric.
         *
         * @param type         the data component type
         * @param defaultValue the fallback value
         * @param <T>          the value type
         * @return the value or default
         *
         * @see net.minecraft.core.component.DataComponentGetter#getOrDefault(DataComponentType, Object)
         */
        default <T> T get(DataComponentType<? extends T> type, T defaultValue) {
            T value = this.get(type);
            return value != null ? value : defaultValue;
        }

        /**
         * @param type the data component type
         * @return is there a value present
         */
        boolean has(DataComponentType<?> type);
    }

    /**
     * A simple implementation of both {@link net.minecraft.core.component.DataComponentMap.Builder} and
     * {@link DataComponentMapGetter}.
     */
    abstract class Builder extends DataComponentMap.Builder implements DataComponentMapGetter {

        public Builder(DataComponentMap.Builder builder) {
            this(builder.map);
        }

        public Builder(Reference2ObjectMap<DataComponentType<?>, Object> map) {
            this.map = map;
        }

        @Override
        public DataComponentMap.Builder addValidator(Consumer<DataComponentMap> newValidator) {
            throw new UnsupportedOperationException();
        }
    }
}
