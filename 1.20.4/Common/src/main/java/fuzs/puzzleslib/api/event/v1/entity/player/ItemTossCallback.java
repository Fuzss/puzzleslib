package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface ItemTossCallback {
    EventInvoker<ItemTossCallback> EVENT = EventInvoker.lookup(ItemTossCallback.class);

    /**
     * Called when an item is tossed from the player inventory, either by pressing 'Q' or by clicking an item stack outside a container screen.
     * <p>This callback can be cancelled so no item entity is added to the level, the item will be lost in that case as it has already been removed from the player inventory.
     *
     * @param itemEntity item entity containing the item stack being tossed, not added to the level yet
     * @param player     the player tossing the item stack
     * @return {@link EventResult#INTERRUPT} to prevent the item from being tossed, nothing will be added to the world and the stack will be lost,
     * {@link EventResult#PASS} to allow the stack to be tossed from the player inventory as usual
     */
    EventResult onItemToss(ItemEntity itemEntity, Player player);
}
