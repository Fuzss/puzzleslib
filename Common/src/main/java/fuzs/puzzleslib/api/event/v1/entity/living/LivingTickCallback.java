package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingTickCallback {
    EventInvoker<LivingTickCallback> EVENT = EventInvoker.lookup(LivingTickCallback.class);

    /**
     * Called at the beginning of {@link LivingEntity#tick()}, allows cancelling ticking the entity.
     *
     * @param entity the entity being ticked
     * @return {@link EventResult#INTERRUPT} to prevent the <code>entity</code> from ticking,
     * {@link EventResult#PASS} to allow vanilla logic to continue
     */
    EventResult onLivingTick(LivingEntity entity);
}
