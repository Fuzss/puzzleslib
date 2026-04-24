package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.common.api.core.v1.context.ItemComponentsContext;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ItemComponentsContextFabricImpl implements ItemComponentsContext {
    private final List<Consumer<DefaultItemComponentEvents.ModifyContext>> itemComponentPatches = new ArrayList<>();

    @Override
    public void registerItemComponentsPatch(Item item, Initializer<Item> initializer) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.registerIfNecessary();
        this.itemComponentPatches.add((DefaultItemComponentEvents.ModifyContext context) -> {
            context.modify(item,
                    (DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item currentItem) -> {
                        initializer.run(new DataComponentGetterImpl(builder),
                                builder,
                                lookupProvider,
                                currentItem.builtInRegistryHolder().key());
                    });
        });
    }

    @Override
    public void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer<Item> initializer) {
        Objects.requireNonNull(itemPredicate, "item predicate is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.registerIfNecessary();
        this.itemComponentPatches.add((DefaultItemComponentEvents.ModifyContext context) -> {
            context.modify(itemPredicate,
                    (DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item currentItem) -> {
                        initializer.run(new DataComponentGetterImpl(builder),
                                builder,
                                lookupProvider,
                                currentItem.builtInRegistryHolder().key());
                    });
        });
    }

    private void registerIfNecessary() {
        if (this.itemComponentPatches.isEmpty()) {
            DefaultItemComponentEvents.MODIFY.register((DefaultItemComponentEvents.ModifyContext context) -> {
                for (Consumer<DefaultItemComponentEvents.ModifyContext> itemComponentPatch : this.itemComponentPatches) {
                    itemComponentPatch.accept(context);
                }
            });
        }
    }

    private record DataComponentGetterImpl(DataComponentMap.Builder builder) implements DataComponentGetter {
        private static final Supplier<?> DEFAULT_VALUE_FALLBACK_SUPPLIER = () -> {
            throw new IllegalStateException("Cannot invoke fallback when type is present");
        };

        @Override
        public @Nullable <T> T get(DataComponentType<? extends T> type) {
            return this.builder.contains(type) ? this.builder.getOrCreate((DataComponentType<T>) type,
                    (Supplier<T>) DEFAULT_VALUE_FALLBACK_SUPPLIER) : null;
        }
    }
}
