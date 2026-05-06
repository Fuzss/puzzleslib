package fuzs.puzzleslib.common.api.client.event.v1.gui;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface HotbarScrollingCallback {
    EventInvoker<HotbarScrollingCallback> EVENT = EventInvoker.lookup(HotbarScrollingCallback.class);

    /**
     * Called when the mouse is scrolling without any {@link net.minecraft.client.gui.screens.Screen} being open, which
     * is used for handling the selected inventory hotbar slot.
     *
     * @param inventory     the player inventory
     * @param oldSlot       the currently selected hotbar slot
     * @param newSlot       the hotbar slot that will be selected next
     * @param scrollAmountX the horizontal scroll amount
     * @param scrollAmountY the vertical scroll amount
     * @return <ul>
     *         <li>{@link EventResultHolder#allow(Object)} to set a custom slot</li>
     *         <li>{@link EventResultHolder#deny(Object)} to prevent the slot from changing at all</li>
     *         <li>{@link EventResultHolder#pass()} to allow the new slot to be set</li>
     *         </ul>
     */
    EventResultHolder<Integer> onHotbarScrolling(Inventory inventory, int oldSlot, int newSlot, double scrollAmountX, double scrollAmountY);
}
