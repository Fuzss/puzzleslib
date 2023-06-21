package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEvents {
    public static final EventInvoker<Tick> TICK = EventInvoker.lookup(Tick.class);
    public static final EventInvoker<Jump> JUMP = EventInvoker.lookup(Jump.class);

    private LivingEvents() {

    }

    @FunctionalInterface
    public interface Tick {

        /**
         * Called at the beginning of {@link LivingEntity#tick()}, allows cancelling ticking the entity.
         *
         * @param entity the entity being ticked
         * @return {@link EventResult#INTERRUPT} to prevent the <code>entity</code> from ticking,
         * {@link EventResult#PASS} to allow vanilla logic to continue
         */
        EventResult onLivingTick(LivingEntity entity);
    }

    @FunctionalInterface
    public interface Jump {

        /**
         * Called when an entity is jumping, allows for modifying the jump height as well as preventing the jump.
         * <p>Additionally looks at {@link LivingEntity#getJumpPower()} and {@link LivingEntity#getJumpBoostPower()} which are the two values that usually make up <code>jumpPower</code>.
         *
         * @param entity    the jumping entity
         * @param jumpPower the current jump power
         * @return {@link EventResult#INTERRUPT} to prevent the entity from jumping,
         * {@link EventResult#PASS} to allow the entity to jump with the value set in <code>jumpPower</code>
         */
        EventResult onLivingJump(LivingEntity entity, DefaultedDouble jumpPower);
    }
}
