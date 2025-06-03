package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationsContext;
import fuzs.puzzleslib.api.client.init.v1.ItemStackDecorator;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.ItemDecoratorRegistry;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class ItemDecorationsContextFabricImpl implements ItemDecorationsContext {

    @Override
    public void registerItemStackDecorator(ItemLike itemLike, ItemStackDecorator itemStackDecorator) {
        Objects.requireNonNull(itemStackDecorator, "item stack decorator is null");
        Objects.requireNonNull(itemLike, "item is null");
        ItemDecoratorRegistry.INSTANCE.register(itemLike, itemStackDecorator);
    }
}
