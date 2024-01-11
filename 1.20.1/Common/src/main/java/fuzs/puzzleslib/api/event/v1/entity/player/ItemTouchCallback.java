package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface ItemTouchCallback {
    EventInvoker<ItemTouchCallback> EVENT = EventInvoker.lookup(ItemTouchCallback.class);

    /**
     * Called when a player touches an {@link ItemEntity} laying on the ground.
     *
     * @param player     the player touching the item entity on the ground
     * @param itemEntity the {@link ItemEntity} that is being touched
     * @return {@link EventResult#ALLOW} to force the item to be removed without there being any attempt at adding to the player inventory,
     * {@link EventResult#DENY} to prevent the item from being picked up,
     * {@link EventResult#PASS} to allow vanilla behavior to proceed where the item will be added to the player inventory, staying on the ground if that fails
     */
    EventResult onItemTouch(Player player, ItemEntity itemEntity);
}
