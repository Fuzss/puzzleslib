package fuzs.puzzleslib.fabric.impl.client.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.client.init.v1.ItemStackDecorator;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.ItemDecoratorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.DrawItemStackOverlayCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.Objects;

public final class ItemDecoratorRegistryImpl implements ItemDecoratorRegistry {
    private static final Multimap<Item, ItemStackDecorator> DECORATORS = HashMultimap.create();

    @Override
    public void register(ItemLike item, ItemStackDecorator itemDecorator) {
        Objects.requireNonNull(item, "item is null");
        Objects.requireNonNull(item.asItem(), "item is null");
        Objects.requireNonNull(itemDecorator, "decorator is null");
        if (DECORATORS.isEmpty()) {
            DrawItemStackOverlayCallback.EVENT.register(ItemDecoratorRegistryImpl::onDrawItemStackOverlay);
        }
        DECORATORS.put(item.asItem(), itemDecorator);
    }

    private static void onDrawItemStackOverlay(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY) {
        Collection<ItemStackDecorator> itemStackDecorators = DECORATORS.get(itemStack.getItem());
        if (!itemStackDecorators.isEmpty()) {
            for (ItemStackDecorator itemDecorator : itemStackDecorators) {
                itemDecorator.renderItemDecorations(guiGraphics, font, itemStack, posX, posY);
            }
        }
    }
}
