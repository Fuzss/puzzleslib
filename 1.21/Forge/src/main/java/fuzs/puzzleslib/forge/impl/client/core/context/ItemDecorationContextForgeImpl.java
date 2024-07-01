package fuzs.puzzleslib.forge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationContext;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.IItemDecorator;

import java.util.Objects;
import java.util.function.BiConsumer;

public record ItemDecorationContextForgeImpl(BiConsumer<ItemLike, IItemDecorator> consumer) implements ItemDecorationContext {

    @Override
    public void registerItemDecorator(DynamicItemDecorator decorator, ItemLike... items) {
        Objects.requireNonNull(decorator, "decorator is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            this.consumer.accept(item, decorator::renderItemDecorations);
        }
    }
}
