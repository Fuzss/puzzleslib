package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.ItemComponentsContext;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.Objects;
import java.util.function.Predicate;

public record ItemComponentsContextNeoForgeImpl(ModifyDefaultComponentsEvent event) implements ItemComponentsContext {

    @Override
    public void registerItemComponentsPatch(Item item, Initializer<Item> initializer) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.event.modify(item, (DataComponentMap.Builder builder) -> {
            // TODO pass proper holder lookup and item resource key when available from the NeoForge event
            initializer.run(builder, builder, RegistryAccess.EMPTY, item.builtInRegistryHolder().key());
        });
    }

    @Override
    public void registerItemComponentsPatch(Predicate<Item> itemPredicate, Initializer<Item> initializer) {
        Objects.requireNonNull(itemPredicate, "item predicate is null");
        Objects.requireNonNull(initializer, "initializer is null");
        this.event.modifyMatching((Item item, DataComponentGetter _) -> {
            // Checking the present components in the filter would be nice, but Fabric does not support it.
            // Also, we can filter for the components when applying changes to the builder as well.
            return itemPredicate.test(item);
        }, (DataComponentMap.Builder builder) -> {
            // TODO pass proper holder lookup and item resource key when available from the NeoForge event
            initializer.run(builder, builder, RegistryAccess.EMPTY, null);
        });
    }
}
