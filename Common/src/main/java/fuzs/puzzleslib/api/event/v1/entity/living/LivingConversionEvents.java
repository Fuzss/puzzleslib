package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public final class LivingConversionEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private LivingConversionEvents() {

    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before an entity is replaced by another through some in-world action like lightning striking a villager turning it into a witch.
         * <p>Can be used to prevent the conversion from occurring, some entities will need their conversion timer to be reset (like drowning husks turning into zombies).
         *
         * @param entity the entity about to be replaced
         * @param result the replacement for the original entity
         */
        EventResult onBeforeLivingConversion(LivingEntity entity, EntityType<? extends LivingEntity> result);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after an entity is replaced by another through some in-world action like lightning striking a villager turning it into a witch.
         * <p>Allows for manually copying custom data between the two entities.
         *
         * @param entity the entity about to be replaced
         * @param result the replacement for the original entity
         */
        void onAfterLivingConversion(LivingEntity entity, LivingEntity result);
    }
}
