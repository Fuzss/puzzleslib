package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.common.api.core.v1.context.ItemComponentsContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.Objects;
import java.util.function.Predicate;

public record ItemComponentsContextNeoForgeImpl(ModifyDefaultComponentsEvent event) implements ItemComponentsContext {

    @Override
    public void registerItemComponentsPatch(Item item, Initializer initializer) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.event.modify(item,
                (DataComponentMap.Builder components, HolderLookup.Provider context, Item itemInstance) -> {
                    initializer.run(components, components, context, itemInstance);
                });
    }

    @Override
    public void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer initializer) {
        Objects.requireNonNull(itemPredicate, "item predicate is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.event.modifyMatching((Item item, DataComponentGetter _) -> {
            // Checking the present components in the filter would be nice, but Fabric does not support it.
            // Also, we can filter for the components when applying changes to the builder as well.
            return itemPredicate.test(item);
        }, (DataComponentMap.Builder components, HolderLookup.Provider context, Item itemInstance) -> {
            initializer.run(components, components, context, itemInstance);
        });
    }
}
