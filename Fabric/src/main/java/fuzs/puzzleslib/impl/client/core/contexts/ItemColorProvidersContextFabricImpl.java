package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ColorProvidersContext;
import fuzs.puzzleslib.api.core.v1.contexts.MultiRegistrationContext;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;

public final class ItemColorProvidersContextFabricImpl implements ColorProvidersContext<Item, ItemColor>, MultiRegistrationContext<Item, ItemColor> {

    @Override
    public void registerColorProvider(ItemColor provider, Item object, Item... objects) {
        this.register(provider, object, objects);
    }

    @Override
    public ItemColor getProviders() {
        return (itemStack, i) -> {
            ItemColor itemColor = ColorProviderRegistry.ITEM.get(itemStack.getItem());
            return itemColor == null ? -1 : itemColor.getColor(itemStack, i);
        };
    }

    @Override
    public void register(Item object, ItemColor type) {
        ColorProviderRegistry.ITEM.register(type, object);
    }
}
