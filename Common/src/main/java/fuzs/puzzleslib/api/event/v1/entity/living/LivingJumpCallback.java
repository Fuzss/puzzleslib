package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingJumpCallback {
    EventInvoker<LivingJumpCallback> EVENT = EventInvoker.lookup(LivingJumpCallback.class);

    /**
     * Called when an entity is jumping, allows for modifying the jump height as well as preventing the jump.
     * <p>
     * Additionally looks at {@link LivingEntity#getJumpPower()} and {@link LivingEntity#getJumpBoostPower()} which are
     * the two values that usually make up <code>jumpPower</code>.
     *
     * @param livingEntity the jumping entity
     * @param jumpPower    the current jump power
     * @return {@link EventResult#INTERRUPT} to prevent the entity from jumping, {@link EventResult#PASS} to allow the
     *         entity to jump with the value set in <code>jumpPower</code>
     */
    EventResult onLivingJump(LivingEntity livingEntity, DefaultedDouble jumpPower);
}
