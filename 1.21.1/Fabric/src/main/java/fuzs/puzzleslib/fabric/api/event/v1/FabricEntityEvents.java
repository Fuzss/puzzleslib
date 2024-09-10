package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.entity.EntityRidingEvents;
import fuzs.puzzleslib.api.event.v1.entity.EntityTickEvents;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.Entity;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity</code> package.
 */
public final class FabricEntityEvents {
    /**
     * Fired when an entity is added to the level on the server after it has been loaded from chunk storage.
     * <p>We do not use {@link net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents#ENTITY_LOAD} as it does not
     * allow for preventing the entity from being added.
     */
    public static final Event<ServerEntityLevelEvents.Load> ENTITY_LOAD = FabricEventFactory.createResult(
            ServerEntityLevelEvents.Load.class);
    /**
     * Fired when an entity is added to the level on the server after it has just been spawned in.
     */
    public static final Event<ServerEntityLevelEvents.Spawn> ENTITY_SPAWN = FabricEventFactory.createResult(
            ServerEntityLevelEvents.Spawn.class);
    /**
     * Fires when a projectile entity impacts on something, either a block or another entity.
     */
    public static final Event<ProjectileImpactCallback> PROJECTILE_IMPACT = FabricEventFactory.createResult(
            ProjectileImpactCallback.class);
    /**
     * Runs when an entity starts riding another entity in {@link Entity#startRiding(Entity)}, allows for preventing
     * that.
     */
    public static final Event<EntityRidingEvents.Start> ENTITY_START_RIDING = FabricEventFactory.createResult(
            EntityRidingEvents.Start.class);
    /**
     * Runs when an entity stops riding another entity in {@link Entity#removeVehicle()}, allows for preventing that.
     */
    public static final Event<EntityRidingEvents.Stop> ENTITY_STOP_RIDING = FabricEventFactory.createResult(
            EntityRidingEvents.Stop.class);
    /**
     * Called before {@link Entity#tick()}, allows cancelling ticking the entity.
     */
    public static final Event<EntityTickEvents.Start> ENTITY_TICK_START = FabricEventFactory.createResult(
            EntityTickEvents.Start.class);
    /**
     * Called after {@link Entity#tick()}.
     */
    public static final Event<EntityTickEvents.End> ENTITY_TICK_END = FabricEventFactory.create(EntityTickEvents.End.class);

    private FabricEntityEvents() {
        // NO-OP
    }
}
