package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

@FunctionalInterface
public interface ProjectileImpactCallback {
    EventInvoker<ProjectileImpactCallback> EVENT = EventInvoker.lookup(ProjectileImpactCallback.class);

    /**
     * Fires when a projectile entity impacts on something, either a block or another entity.
     *
     * @param projectile the projectile about to impact
     * @param hitResult  {@link HitResult} context
     * @return {@link EventResult#INTERRUPT} tp prevent the impact from being processed (<code>Projectile#onHit(HitResult)</code> does not run), the projectile will just keep flying,
     * {@link EventResult#PASS} to let vanilla behavior process the impact, destroying the projectile as a result
     */
    EventResult onProjectileImpact(Projectile projectile, HitResult hitResult);
}
