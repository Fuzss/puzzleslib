package fuzs.puzzleslib.api.client.init.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

/**
 * a hook patched in right at the end of {@link net.minecraft.client.renderer.entity.ItemRenderer#renderGuiItemDecorations} with basically the same arguments
 *
 * <p>required as a wrapper for the Forge equivalent <code>net.minecraftforge.client.IItemDecorator</code>
 */
@FunctionalInterface
public interface DynamicItemDecorator {

    /**
     * render a decoration for an item
     *
     * @param poseStack     the pose stack currently used for rendering
     * @param font          the font renderer instance
     * @param stack         the item stack this decoration is rendered for
     * @param itemPosX      x-position of <code>stack</code> on the screen
     * @param itemPosY      x-position of <code>stack</code> on the screen
     * @return              return true if any render state has been altered (rendering text using <code>font</code> does that too), so that we can reset that afterwards
     */
    boolean renderItemDecorations(PoseStack poseStack, Font font, ItemStack stack, int itemPosX, int itemPosY);
}
