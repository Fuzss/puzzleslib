package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ItemTooltipCallback {
    EventInvoker<ItemTooltipCallback> EVENT = EventInvoker.lookup(ItemTooltipCallback.class);

    /**
     * Called at the end of item tooltip gathering in {@link ItemStack#getTooltipLines(Player, TooltipFlag)}.
     * <p>Allows for both appending additional tooltip lines, and removing / replacing existing lines.
     *
     * @param stack   the item stack owning this tooltip
     * @param player  the player looking at the tooltip, is null when search trees are created
     * @param lines   the tooltip lines
     * @param context tooltip flag context
     */
    void onItemTooltip(ItemStack stack, @Nullable Player player, List<Component> lines, TooltipFlag context);
}
