package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingChangeTargetCallback {
    EventInvoker<LivingChangeTargetCallback> EVENT = EventInvoker.lookup(LivingChangeTargetCallback.class);

    /**
     * Called when a {@link net.minecraft.world.entity.Mob} sets a new target.
     *
     * @param entity the entity setting a new target
     * @param target the target to be set, can be <code>null</code>
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the target from changing</li>
     *         <li>{@link EventResult#PASS PASS} to allow the target to change to the set value</li>
     *         </ul>
     */
    EventResult onLivingChangeTarget(LivingEntity entity, DefaultedValue<LivingEntity> target);
}
