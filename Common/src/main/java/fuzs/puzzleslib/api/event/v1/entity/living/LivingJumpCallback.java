package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingJumpCallback {
    EventInvoker<LivingJumpCallback> EVENT = EventInvoker.lookup(LivingJumpCallback.class);

    /**
     * Called when an entity is jumping, allows for modifying the jump height as well as preventing the jump.
     *
     * @param livingEntity the jumping entity
     * @param jumpPower    the current jump power
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT} to prevent the entity from jumping</li>
     *         <li>{@link EventResult#PASS} to allow the entity to jump with the value set jump power</li>
     *         </ul>
     */
    EventResult onLivingJump(LivingEntity livingEntity, MutableDouble jumpPower);
}
