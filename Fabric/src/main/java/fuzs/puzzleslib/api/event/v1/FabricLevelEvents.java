package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.level.Explosion;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.level</code> package.
 */
public final class FabricLevelEvents {
    /**
     * Fired when an entity falls onto a block of farmland and in the process would trample on it, turning the block into dirt and destroying potential crops.
     */
    public static final Event<BlockEvents.FarmlandTrample> FARMLAND_TRAMPLE = FabricEventFactory.createResult(BlockEvents.FarmlandTrample.class);
    /**
     * Called just before an {@link Explosion} is about to be executed for a level, allows for preventing that explosion.
     */
    public static final Event<ExplosionEvents.Start> EXPLOSION_START = FabricEventFactory.createResult(ExplosionEvents.Start.class);
    /**
     * Called just before entities affected by an ongoing explosion are processed, meaning before they are hurt and knocked back.
     */
    public static final Event<ExplosionEvents.Detonate> EXPLOSION_DETONATE = FabricEventFactory.create(ExplosionEvents.Detonate.class);
}
