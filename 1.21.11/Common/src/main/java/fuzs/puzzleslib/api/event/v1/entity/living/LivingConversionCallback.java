package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface LivingConversionCallback {
    EventInvoker<LivingConversionCallback> EVENT = EventInvoker.lookup(LivingConversionCallback.class);

    /**
     * Called after an entity is replaced by another through some in-world action like lightning striking a villager
     * turning it into a witch.
     * <p>
     * The original being removed and the new entity being added to the level yet is undefined.
     * <p>
     * Allows for manually copying custom data between the two entities.
     *
     * @param originalEntity the entity about to be replaced
     * @param newEntity      the replacement for the original entity
     */
    void onLivingConversion(LivingEntity originalEntity, LivingEntity newEntity);
}
