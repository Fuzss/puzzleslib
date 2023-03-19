package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.world.FarmlandTrampleCallback;
import net.fabricmc.fabric.api.event.Event;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.world</code> package.
 */
public final class FabricWorldEvents {
    /**
     * Fired when an entity falls onto a block of farmland and in the process would trample on it, turning the block into dirt and destroying potential crops.
     */
    public static final Event<FarmlandTrampleCallback> FARMLAND_TRAMPLE = FabricEventFactory.createResult(FarmlandTrampleCallback.class);
}
