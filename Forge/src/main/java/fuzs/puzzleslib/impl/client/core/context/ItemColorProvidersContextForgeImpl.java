package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.core.v1.context.ColorProvidersContext;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.function.BiConsumer;

public record ItemColorProvidersContextForgeImpl(BiConsumer<ItemColor, ItemLike> consumer,
                                                 ItemColors itemColors) implements ColorProvidersContext<Item, ItemColor> {

    @Override
    public void registerColorProvider(ItemColor provider, Item... items) {
        Objects.requireNonNull(provider, "provider is null");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkPositionIndex(0, items.length, "items is empty");
        for (ItemLike item : items) {
            Objects.requireNonNull(item, "item is null");
            this.consumer.accept(provider, item);
        }
    }

    @Override
    public ItemColor getProviders() {
        return this.itemColors::getColor;
    }
}
