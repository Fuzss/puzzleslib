package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BonemealCallback {
    EventInvoker<BonemealCallback> EVENT = EventInvoker.lookup(BonemealCallback.class);

    /**
     * Called when a bone meal is used on a block by the player, a dispenser, or a farmer villager.
     * <p>Useful for adding custom bone meal behavior to blocks, or for cancelling vanilla interactions.
     *
     * @param level level bone meal event occurs in
     * @param pos position bone meal is applied to
     * @param block block state bone meal is applied to
     * @param stack the bone meal stack
     * @return  {@link EventResult#PASS} to continue with vanilla,
     *          {@link EventResult#ALLOW} to set as handled and let vanilla show particles + arm swing animation,
     *          {@link EventResult#DENY} to not do anything
     */
    EventResult onBonemeal(Level level, BlockPos pos, BlockState block, ItemStack stack);
}
