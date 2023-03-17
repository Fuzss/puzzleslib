package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ColorProvidersContext;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;

import java.util.Objects;

public final class ItemColorProvidersContextFabricImpl implements ColorProvidersContext<Item, ItemColor> {

    @Override
    public void registerColorProvider(ItemColor provider, Item object, Item... objects) {
        Objects.requireNonNull(provider, "provider is null");
        this.registerItemColorProvider(object, provider);
        Objects.requireNonNull(objects, "items is null");
        for (Item item : objects) {
            this.registerItemColorProvider(item, provider);
        }
    }

    private void registerItemColorProvider(Item item, ItemColor provider) {
        Objects.requireNonNull(item, "item is null");
        ColorProviderRegistry.ITEM.register(provider, item);
    }

    @Override
    public ItemColor getProviders() {
        return (itemStack, i) -> {
            ItemColor itemColor = ColorProviderRegistry.ITEM.get(itemStack.getItem());
            return itemColor == null ? -1 : itemColor.getColor(itemStack, i);
        };
    }
}
