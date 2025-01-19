package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BreakSpeedCallback {
    EventInvoker<BreakSpeedCallback> EVENT = EventInvoker.lookup(BreakSpeedCallback.class);

    /**
     * Called when the player attempts to harvest a block in {@link Player#getDestroySpeed(BlockState)}.
     *
     * @param player     the player breaking <code>state</code>
     * @param blockState the block state being broken
     * @param breakSpeed the speed at which the block is broken, usually a value around 1.0
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the block from breaking, effectively setting the break speed to -1.0</li>
     *         <li>{@link EventResult#PASS PASS} to allow the block to be broken at the defined break speed</li>
     *         </ul>
     */
    EventResult onBreakSpeed(Player player, BlockState blockState, DefaultedFloat breakSpeed);
}
