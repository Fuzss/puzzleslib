package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import net.fabricmc.fabric.api.event.Event;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity</code> package.
 */
public final class FabricEntityEvents {
    /**
     * Fires when a projectile entity impacts on something, either a block or another entity.
     */
    public static final Event<ProjectileImpactCallback> PROJECTILE_IMPACT = FabricEventFactory.createResult(ProjectileImpactCallback.class);

    private FabricEntityEvents() {

    }
}
