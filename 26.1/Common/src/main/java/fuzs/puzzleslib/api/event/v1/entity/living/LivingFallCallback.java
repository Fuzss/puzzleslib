package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingFallCallback {
    EventInvoker<LivingFallCallback> EVENT = EventInvoker.lookup(LivingFallCallback.class);

    /**
     * Called right at the beginning of {@link LivingEntity#causeFallDamage}, allows the method to be cancelled to
     * prevent fall damage.
     * <p>
     * Can also be used to modify the distance the entity has fallen used for damage calculation, as well as the damage
     * multiplier defined by the block the entity is falling on to.
     *
     * @param livingEntity     the falling entity
     * @param fallDistance     the distance the entity has fallen for calculating fall damage
     * @param damageMultiplier damage multiplier depending on the type of block the entity is falling on
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the entity taking any falling damage</li>
     *         <li>{@link EventResult#PASS PASS} to allow vanilla behavior to continue executing with values set by the event</li>
     *         </ul>
     */
    EventResult onLivingFall(LivingEntity livingEntity, MutableDouble fallDistance, MutableFloat damageMultiplier);
}
