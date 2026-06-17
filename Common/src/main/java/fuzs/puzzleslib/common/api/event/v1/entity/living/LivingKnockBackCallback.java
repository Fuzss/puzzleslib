package fuzs.puzzleslib.common.api.event.v1.entity.living;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.data.MutableDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingKnockBackCallback {
    EventInvoker<LivingKnockBackCallback> EVENT = EventInvoker.lookup(LivingKnockBackCallback.class);

    /**
     * Called before an entity is knocked-back in
     * {@link LivingEntity#knockback(double, double, double, DamageSource, float, boolean)}, allows for preventing the
     * knock-back.
     *
     * @param livingEntity      the living entity that will be knocked back
     * @param knockbackStrength the strength that will be used to knock back the entity, values usually range between
     *                          0.5-1.0, 0.0 or negative values prevent any knock-back, value is additionally reduced by
     *                          {@link net.minecraft.world.entity.ai.attributes.Attributes#KNOCKBACK_RESISTANCE}
     * @param ratioX            the x-direction to knock back in; will be scaled by the strength
     * @param ratioZ            the z-direction to knock back in; will be scaled by the strength
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the knock-back from happening</li>
     *         <li>{@link EventResult#PASS PASS} to allow the knock-back to happen with values supplied by the event</li>
     *         </ul>
     */
    EventResult onLivingKnockBack(LivingEntity livingEntity, MutableDouble knockbackStrength, MutableDouble ratioX, MutableDouble ratioZ);
}
