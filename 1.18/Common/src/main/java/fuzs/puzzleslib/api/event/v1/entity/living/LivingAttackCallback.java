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
     * @param entity the entity that is attacked
     * @param source the {@link DamageSource} {@code entity} has been attacked by
     * @param amount the amount of damage {@code entity} is attacked with
     * @return {@link EventResult#INTERRUPT} to prevent the attack from happening, dealing no damage,
     * {@link EventResult#PASS} to allow the entity to be attacked and to take damage
     */
    EventResult onLivingAttack(LivingEntity entity, DamageSource source, float amount);
}
