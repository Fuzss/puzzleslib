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
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} tp prevent the impact from being processed, meaning {@link Projectile#onHit(HitResult)} does not run, the projectile will just keep flying</li>
     *         <li>{@link EventResult#PASS PASS} to let vanilla behavior process the impact, destroying the projectile as a
     *         result</li>
     *         </ul>
     */
    EventResult onProjectileImpact(Projectile projectile, HitResult hitResult);
}
