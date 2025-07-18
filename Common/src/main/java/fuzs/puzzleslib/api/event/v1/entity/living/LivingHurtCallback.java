package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingHurtCallback {
    EventInvoker<LivingHurtCallback> EVENT = EventInvoker.lookup(LivingHurtCallback.class);

    /**
     * Called right before any reduction in damage due to e.g. armour is done, cancelling prevents any damage / armour
     * durability being taken.
     *
     * @param livingEntity the entity being hurt
     * @param damageSource the damage source the entity is hurt by
     * @param damageAmount the amount the entity is hurt by; can be modified and will only be applied when the event is
     *                     not interrupted
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent this entity from being hurt</li>
     *         <li>{@link EventResult#PASS PASS} will make vanilla continue to execute</li>
     *         </ul>
     */
    EventResult onLivingHurt(LivingEntity livingEntity, DamageSource damageSource, MutableFloat damageAmount);
}
