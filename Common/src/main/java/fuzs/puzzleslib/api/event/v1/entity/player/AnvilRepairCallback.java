package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface AnvilRepairCallback {
    EventInvoker<AnvilRepairCallback> EVENT = EventInvoker.lookup(AnvilRepairCallback.class);

    /**
     * Called when the player takes the output item from an anvil, used to determine the chance by which the anvil will break down one stage.
     *
     * @param player      the player interacting with the anvil
     * @param left        left input item stack
     * @param right       right input item stack
     * @param output      the output stack the player is about to take
     * @param breakChance chance for the anvil to break down one stage
     */
    void onAnvilRepair(Player player, ItemStack left, ItemStack right, ItemStack output, MutableFloat breakChance);
}
