package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ItemDecorationsContext;
import fuzs.puzzleslib.api.client.init.v1.ItemStackDecorator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

import java.util.Objects;

public record ItemDecorationsContextNeoForgeImpl(RegisterItemDecorationsEvent evt) implements ItemDecorationsContext {

    @Override
    public void registerItemStackDecorator(ItemLike itemLike, ItemStackDecorator itemStackDecorator) {
        Objects.requireNonNull(itemStackDecorator, "item stack decorator is null");
        Objects.requireNonNull(itemLike, "item is null");
        this.evt.register(itemLike, (GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY) -> {
            itemStackDecorator.renderItemDecorations(guiGraphics, font, itemStack, posX, posY);
            return false;
        });
    }
}
