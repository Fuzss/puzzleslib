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
     * Called right before any reduction on damage due to e.g. armor are done, cancelling prevents any damage / armor durability being taken.
     * <p>This event runs at the beginning of {@link LivingEntity#actuallyHurt(DamageSource, float)}.
     *
     * @param entity the entity being hurt
     * @param source damage source entity is hurt by
     * @param amount amount hurt, can be modified and will only be applied when the event is not interrupted
     * @return {@link EventResult#INTERRUPT} to prevent this entity from being hurt,
     * {@link EventResult#PASS} will make vanilla continue to execute
     */
    EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount);
}
