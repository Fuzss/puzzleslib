package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingAttackCallback {
    EventInvoker<LivingAttackCallback> EVENT = EventInvoker.lookup(LivingAttackCallback.class);

    /**
     * Fires when a {@link LivingEntity} is attacked, allows for cancelling that attack.
     *
     * @param livingEntity the entity that is attacked
     * @param damageSource the {@link DamageSource} the entity has been attacked by
     * @param damageAmount the amount of damage the entity is attacked with
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the attack from happening, dealing no damage</li>
     *         <li>{@link EventResult#PASS PASS} to allow the entity to be attacked and to take damage</li>
     *         </ul>
     */
    EventResult onLivingAttack(LivingEntity livingEntity, DamageSource damageSource, float damageAmount);
}
