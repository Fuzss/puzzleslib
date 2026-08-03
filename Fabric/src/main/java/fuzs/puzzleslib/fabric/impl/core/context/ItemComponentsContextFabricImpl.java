package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.common.api.core.v1.context.ItemComponentsContext;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ItemComponentsContextFabricImpl implements ItemComponentsContext {
    private final List<Consumer<DefaultItemComponentEvents.ModifyContext>> itemComponentPatches = new ArrayList<>();

    @Override
    public void registerItemComponentsPatch(Item item, Initializer initializer) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.registerIfNecessary();
        this.itemComponentPatches.add((DefaultItemComponentEvents.ModifyContext context) -> {
            context.modify(item,
                    (DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item itemInstance) -> {
                        initializer.run(builder::get, builder, lookupProvider, itemInstance);
                    });
        });
    }

    @Override
    public void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer initializer) {
        Objects.requireNonNull(itemPredicate, "item predicate is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.registerIfNecessary();
        this.itemComponentPatches.add((DefaultItemComponentEvents.ModifyContext context) -> {
            context.modify(itemPredicate,
                    (DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item itemInstance) -> {
                        initializer.run(builder::get, builder, lookupProvider, itemInstance);
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
}
