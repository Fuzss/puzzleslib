package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntConsumer;

@FunctionalInterface
public interface CreateGrindstoneResultCallback {
    EventInvoker<CreateGrindstoneResultCallback> EVENT = EventInvoker.lookup(CreateGrindstoneResultCallback.class);

    /**
     * Called after a result item is created from the two input slots in a grindstone via
     * {@link GrindstoneMenu#createResult()}.
     *
     * @param player                the player using the grindstone
     * @param primaryItemStack      the item stack placed in the left input slot
     * @param secondaryItemStack    the item stack placed in the right input slot
     * @param outputItemStack       the item computed by vanilla for the result slot
     * @param experiencePointReward the experience points to gain from this operation
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent vanilla logic from running, the output item stack will not be updated</li>
     *         <li>{@link EventResult#PASS PASS} to set a custom result from the specified output item stack</li>
     *         </ul>
     */
    EventResult onCreateGrindstoneResult(Player player, ItemStack primaryItemStack, ItemStack secondaryItemStack, MutableValue<ItemStack> outputItemStack, IntConsumer experiencePointReward);
}
