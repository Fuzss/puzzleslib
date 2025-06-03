package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * A hook patched in right at the end of
 * {@link net.minecraft.client.gui.GuiGraphics#renderItemDecorations(Font, ItemStack, int, int, String)} with basically
 * the same arguments.
 */
@FunctionalInterface
public interface ItemStackDecorator {

    /**
     * Renders a decoration for an item stack.
     *
     * @param guiGraphics the gui graphics
     * @param font        the font renderer
     * @param itemStack   the item stack
     * @param posX        the x-position of the item stack
     * @param posY        the y-position of the item stack
     */
    void renderItemDecorations(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY);
}
