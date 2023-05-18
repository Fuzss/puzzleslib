package fuzs.puzzleslib.impl.client.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.ItemDecoratorRegistry;
import fuzs.puzzleslib.api.client.init.v1.DynamicItemDecorator;
import net.minecraft.client.gui.Font;
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

    public static void render(PoseStack poseStack, Font font, ItemStack stack, int itemPosX, int itemPosY) {
        Collection<DynamicItemDecorator> dynamicItemDecorators = DECORATORS.get(stack.getItem());
        if (dynamicItemDecorators.isEmpty()) return;
        resetRenderState();
        for (DynamicItemDecorator itemDecorator : dynamicItemDecorators) {
            if (itemDecorator.renderItemDecorations(poseStack, font, stack, itemPosX, itemPosY)) {
                resetRenderState();
            }
        }
    }

    private static void resetRenderState() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
