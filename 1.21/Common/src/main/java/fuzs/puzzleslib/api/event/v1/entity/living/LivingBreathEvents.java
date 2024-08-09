package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.world.entity.LivingEntity;

public final class LivingBreathEvents {
    public static final EventInvoker<Breathe> BREATHE = EventInvoker.lookup(Breathe.class);
    public static final EventInvoker<Drown> DROWN = EventInvoker.lookup(Drown.class);

    private LivingBreathEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Breathe {

        /**
         * Runs when the game updates an entity's air supply depending on if the entity can currently breathe or not.
         * <p>Allows for updating the air supply with a custom value or for preventing any update at all.
         *
         * @param entity       the entity to update air supply for
         * @param airAmount    amount to change air supply by; a negative value will consume air like when the entity is
         *                     underwater, while a positive value will replenish air like when the entity is able to
         *                     breathe
         * @param canRefillAir decides if the entity can refill air when not inside a fluid or in a bubble column
         * @param canLoseAir   decides if the entity can lose air when submerged and no protection (like water
         *                     breathing) is available
         * @return {@link EventResult#INTERRUPT} to prevent the air supply from changing, {@link EventResult#PASS} to
         *         allow air supply to update with value supplied by the event
         */
        EventResult onLivingBreathe(LivingEntity entity, DefaultedInt airAmount, boolean canRefillAir, boolean canLoseAir);
    }

    @FunctionalInterface
    public interface Drown {

        /**
         * Runs before the game checks if an entity that is submerged should be damaged from drowning.
         * <p>
         *     Allows for both preventing an entity from drowning and inflicting drowning damage when vanilla would not
         * do so.
         *
         * @param entity     the entity that is drowning
         * @param airSupply  current air supply of the entity, retrieved from {@link LivingEntity#getAirSupply()}
         * @param isDrowning would the entity receive drowning damage per vanilla drowning rules (every 20 ticks after
         *                   air has run out)
         * @return {@link EventResult#ALLOW} to make the entity receive drowning damage, which includes spawning bubble
         *         particles, {@link EventResult#DENY} to prevent the entity from receiving any drowning damage,
         *         {@link EventResult#PASS} to let the drowning behavior defined by <code>isDrowning</code> run
         */
        EventResult onLivingDrown(LivingEntity entity, int airSupply, boolean isDrowning);
    }
}
