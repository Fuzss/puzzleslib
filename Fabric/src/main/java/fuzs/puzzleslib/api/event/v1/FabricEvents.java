package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;

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
    /**
     * Called before a result item is generated from the two input slots in an anvil in {@link AnvilMenu#createResult()}.
     */
    public static final Event<AnvilUpdateCallback> ANVIL_UPDATE = FabricEventFactory.createResult(AnvilUpdateCallback.class);
}
