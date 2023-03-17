package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ItemDecorationContext;
import fuzs.puzzleslib.api.client.events.v2.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.registration.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class ItemDecorationContextFabricImpl implements ItemDecorationContext {

    @Override
    public void registerItemDecorator(DynamicItemDecorator decorator, ItemLike object, ItemLike... objects) {
        Objects.requireNonNull(decorator, "item decorator is null");
        Objects.requireNonNull(object, "item is null");
        ItemDecoratorRegistry.INSTANCE.register(object, decorator);
        Objects.requireNonNull(objects, "items is null");
        for (ItemLike item : objects) {
            Objects.requireNonNull(item, "item is null");
            ItemDecoratorRegistry.INSTANCE.register(item, decorator);
        }
    }
}
