package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface LookingAtEndermanCallback {
    EventInvoker<LookingAtEndermanCallback> EVENT = EventInvoker.lookup(LookingAtEndermanCallback.class);

    /**
     * Called in {@link EnderMan#isBeingStaredBy(Player)}, to allow for custom behavior when looking at an enderman.
     * <p>
     * Mainly useful for supporting custom mask blocks for safely looking at an enderman.
     * <p>
     * Does not allow for altering vanilla behavior of {@link net.minecraft.world.level.block.Blocks#CARVED_PUMPKIN}.
     *
     * @param enderman the enderman being look at
     * @param player   the player looking at the enderman
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the enderman from being angered and targeting the player</li>
     *         <li>{@link EventResult#PASS PASS} to let the enderman become angry</li>
     *         </ul>
     */
    EventResult onLookingAtEnderManCallback(EnderMan enderman, Player player);
}
