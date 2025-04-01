package fuzs.puzzleslib.fabric.impl.client.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.ItemDecoratorRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.Objects;

public final class ItemDecoratorRegistryImpl implements ItemDecoratorRegistry {
    private static final Multimap<Item, DynamicItemDecorator> DECORATORS = HashMultimap.create();

    @Override
    public void register(ItemLike item, DynamicItemDecorator itemDecorator) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(item.asItem(), "item is null");
        Objects.requireNonNull(itemDecorator, "decorator is null");
        DECORATORS.put(item.asItem(), itemDecorator);
    }

    public static void render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int itemPosX, int itemPosY) {
        Collection<DynamicItemDecorator> dynamicItemDecorators = DECORATORS.get(itemStack.getItem());
        if (!dynamicItemDecorators.isEmpty()) {
            for (DynamicItemDecorator itemDecorator : dynamicItemDecorators) {
                itemDecorator.renderItemDecorations(guiGraphics, font, itemStack, itemPosX, itemPosY);
            }
        }
    }
}
