package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationsContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class ItemDecorationsContextFabricImpl implements ItemDecorationsContext {

    @Override
    public void registerItemDecorator(DynamicItemDecorator decorator, ItemLike... items) {
        Objects.requireNonNull(decorator, "decorator is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            ItemDecoratorRegistry.INSTANCE.register(item, decorator);
        }
    }
}
