package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface UseBoneMealCallback {
    EventInvoker<UseBoneMealCallback> EVENT = EventInvoker.lookup(UseBoneMealCallback.class);

    /**
     * Called when a bone meal is used on a block by the player, a dispenser, or a farmer villager.
     * <p>
     * Useful for adding custom bone meal behaviour to blocks or for cancelling vanilla interactions.
     *
     * @param level      the level the bone meal event occurs in
     * @param blockPos   the position the bone meal is applied to
     * @param blockState the block state the bone meal is applied to
     * @param itemStack  the bone meal item stack
     * @return <ul>
     *         <li>{@link EventResult#PASS PASS} to continue with vanilla</li>
     *         <li>{@link EventResult#ALLOW ALLOW} to set as handled and let vanilla show particles + arm swing animation</li>
     *         <li>{@link EventResult#DENY DENY} to not do anything</li>
     *         </ul>
     */
    EventResult onUseBoneMeal(Level level, BlockPos blockPos, BlockState blockState, ItemStack itemStack);
}
