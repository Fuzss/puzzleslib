package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingFallCallback {
    EventInvoker<LivingFallCallback> EVENT = EventInvoker.lookup(LivingFallCallback.class);

    /**
     * Called right at the beginning of {@link LivingEntity#causeFallDamage}, allows the method to be cancelled to prevent fall damage.
     * <p>Can also be used to modify the distance the entity has fallen used for damage calculation, as well as the damage multiplier defined by the block the entity is falling on to.
     *
     * @param entity           the falling entity
     * @param fallDistance     the distance the entity has fallen for calculating fall damage
     * @param damageMultiplier damage multiplier depending on the type of block <code>entity</code> is falling on
     * @return {@link EventResult#INTERRUPT} to prevent the entity taking any falling damage,
     * {@link EventResult#PASS} to allow vanilla behavior to continue executing with possibly new values in <code>fallDistance</code> and <code>damageMultiplier</code>
     */
    EventResult onLivingFall(LivingEntity entity, MutableFloat fallDistance, MutableFloat damageMultiplier);
}
