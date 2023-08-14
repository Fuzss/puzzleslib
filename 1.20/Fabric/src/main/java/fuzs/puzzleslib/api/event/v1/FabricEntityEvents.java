package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import net.fabricmc.fabric.api.event.Event;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity</code> package.
 */
public final class FabricEntityEvents {
    /**
     * Fired when an entity is added to the level on the server.
     * <p>We do not use {@link net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents#ENTITY_LOAD} as it does not allow for preventing the entity from being added.
     */
    public static final Event<ServerEntityLevelEvents.Load> ENTITY_LOAD = FabricEventFactory.createResult(ServerEntityLevelEvents.Load.class);
    /**
     * Fired when an entity is added to the level on the server.
     * <p>We do not use {@link net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents#ENTITY_LOAD} as it does not allow for preventing the entity from being added.
     */
    public static final Event<ServerEntityLevelEvents.LoadV2> ENTITY_LOAD_V2 = FabricEventFactory.createResult(ServerEntityLevelEvents.LoadV2.class);
    /**
     * Fires when a projectile entity impacts on something, either a block or another entity.
     */
    public static final Event<ProjectileImpactCallback> PROJECTILE_IMPACT = FabricEventFactory.createResult(ProjectileImpactCallback.class);

    private FabricEntityEvents() {

    }
}
