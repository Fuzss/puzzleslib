package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.player.Player;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event</code> package.
 */
public final class FabricEvents {
    /**
     * Fires at the beginning of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.Start> PLAYER_TICK_START = FabricEventFactory.create(PlayerTickEvents.Start.class);
    /**
     * Fires at the end of {@link Player#tick()}.
     */
    public static final Event<PlayerTickEvents.End> PLAYER_TICK_END = FabricEventFactory.create(PlayerTickEvents.End.class);
}
