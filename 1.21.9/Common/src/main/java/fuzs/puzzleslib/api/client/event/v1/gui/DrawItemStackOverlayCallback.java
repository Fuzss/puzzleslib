package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

@FunctionalInterface
public interface DrawItemStackOverlayCallback {

    static EventInvoker<DrawItemStackOverlayCallback> drawItemStackOverlay(ItemLike item) {
        Objects.requireNonNull(item, "item is null");
        return EventInvoker.lookup(DrawItemStackOverlayCallback.class, item.asItem());
    }

    /**
     * /** Fires at the end of {@link GuiGraphics#renderItemDecorations(Font, ItemStack, int, int, String)} and allows
     * for drawing custom item stack decorations.
     * <p>
     * In vanilla these are: durability bar, cooldown overlay and stack count.
     *
     * @param guiGraphics the gui graphics
     * @param font        the font renderer
     * @param itemStack   the item stack
     * @param posX        the x-position of the item stack
     * @param posY        the y-position of the item stack
     */
    void onDrawItemStackOverlay(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY);
}
