package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingDeathCallback {
    EventInvoker<LivingDeathCallback> EVENT = EventInvoker.lookup(LivingDeathCallback.class);

    /**
     * Fires whenever a living entity dies, allows for preventing the death.
     *
     * @param entity the entity that has been killed
     * @param source the {@link DamageSource} the entity has been killed by
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the death from happening, the entity will stay alive</li>
     *         <li>{@link EventResult#PASS PASS} to allow vanilla logic to continue executing</li>
     *         </ul>
     */
    EventResult onLivingDeath(LivingEntity entity, DamageSource source);
}
