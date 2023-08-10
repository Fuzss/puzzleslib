package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class LivingEvents {
    public static final EventInvoker<Tick> TICK = EventInvoker.lookup(Tick.class);
    public static final EventInvoker<Jump> JUMP = EventInvoker.lookup(Jump.class);
    public static final EventInvoker<Visibility> VISIBILITY = EventInvoker.lookup(Visibility.class);

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

    @FunctionalInterface
    public interface Visibility {

        /**
         * Called in {@link LivingEntity#getVisibilityPercent(Entity)} when an entity is trying to be targeted by another entity for applying a given percentage to the looking entity's original visibility range.
         *
         * @param entity               the entity trying to be targeted
         * @param lookingEntity        the looking entity that is trying to target <code>entity</code>
         * @param visibilityPercentage the visibility percentage multiplied with the <code>lookingEntity</code>'s targeting range
         */
        void onLivingVisibility(LivingEntity entity, @Nullable Entity lookingEntity, MutableDouble visibilityPercentage);
    }
}
