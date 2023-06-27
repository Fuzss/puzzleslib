package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * A hook patched in right at the end of {@link net.minecraft.client.gui.GuiGraphics#renderItemDecorations(Font, ItemStack, int, int, String)} with basically the same arguments.
 *
 * <p>required as a wrapper for the Forge equivalent <code>net.minecraftforge.client.IItemDecorator</code>
 */
@FunctionalInterface
public interface DynamicItemDecorator {

    /**
     * render a decoration for an item
     *
     * @param guiGraphics the gui graphics component
     * @param font          the font renderer instance
     * @param stack         the item stack this decoration is rendered for
     * @param itemPosX      x-position of <code>stack</code> on the screen
     * @param itemPosY      x-position of <code>stack</code> on the screen
     * @return              return true if any render state has been altered (rendering text using <code>font</code> does that too), so that we can reset that afterwards
     */
    boolean renderItemDecorations(GuiGraphics guiGraphics, Font font, ItemStack stack, int itemPosX, int itemPosY);
}
