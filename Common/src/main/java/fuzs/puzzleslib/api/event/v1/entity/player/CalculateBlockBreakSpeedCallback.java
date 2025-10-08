package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface CalculateBlockBreakSpeedCallback {
    EventInvoker<CalculateBlockBreakSpeedCallback> EVENT = EventInvoker.lookup(CalculateBlockBreakSpeedCallback.class);

    /**
     * Called when the player attempts to harvest a block in {@link Player#getDestroySpeed(BlockState)}.
     *
     * @param player     the player breaking the block
     * @param blockState the block state being broken
     * @param breakSpeed the speed at which the block is broken, usually a value around 1.0
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the block from breaking, effectively setting the break speed to -1.0</li>
     *         <li>{@link EventResult#PASS PASS} to allow the block to be broken at the defined break speed</li>
     *         </ul>
     */
    EventResult onCalculateBlockBreakSpeed(Player player, BlockState blockState, MutableFloat breakSpeed);
}
