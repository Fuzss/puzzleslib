package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class LivingEvents {
    public static final EventInvoker<Tick> TICK = EventInvoker.lookup(Tick.class);
    public static final EventInvoker<Jump> JUMP = EventInvoker.lookup(Jump.class);
    public static final EventInvoker<Visibility> VISIBILITY = EventInvoker.lookup(Visibility.class);
    public static final EventInvoker<Breathe> BREATHE = EventInvoker.lookup(Breathe.class);
    public static final EventInvoker<Drown> DROWN = EventInvoker.lookup(Drown.class);

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

    @FunctionalInterface
    public interface Breathe {

        /**
         * Runs when the game updates an entity's air supply depending on if the entity can currently breathe or not.
         * <p>Allows for updating the air supply with a custom value or for preventing any update at all.
         *
         * @param entity           the entity to update air supply for
         * @param airAmount        amount to change air supply by
         * @param canRefillAir     decides if the entity can refill air when not inside a fluid or in a bubble column
         * @param canLoseAir       decides if the entity can lose air when submerged and no protection (like water breathing) is available
         * @return {@link EventResult#INTERRUPT} to prevent the air supply from changing,
         * {@link EventResult#PASS} to allow air supply to update with value supplied by the event
         */
        EventResult onLivingBreathe(LivingEntity entity, DefaultedInt airAmount, boolean canRefillAir, boolean canLoseAir);
    }

    @FunctionalInterface
    public interface Drown {

        /**
         * Runs before the game checks if an entity that is submerged should be damaged from drowning.
         * <p>Allows for both preventing an entity from drowning and inflicting drowning damage when vanilla would not do so.
         *
         * @param entity     the entity that is drowning
         * @param airSupply  current air supply of the entity, retrieved from {@link LivingEntity#getAirSupply()}
         * @param isDrowning would the entity receive drowning damage per vanilla drowning rules (every 20 ticks after air has run out)
         * @return {@link EventResult#ALLOW} to make the entity receive drowning damage, which includes spawning bubble particles,
         * {@link EventResult#DENY} to prevent the entity from receiving any drowning damage,
         * {@link EventResult#PASS} to let the drowning behavior defined by <code>isDrowning</code> run
         */
        EventResult onLivingDrown(LivingEntity entity, int airSupply, boolean isDrowning);
    }
}
