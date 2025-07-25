package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * TODO rename to CalculateLivingVisibilityCallback
 */
@FunctionalInterface
public interface LivingVisibilityCallback {
    EventInvoker<LivingVisibilityCallback> EVENT = EventInvoker.lookup(LivingVisibilityCallback.class);

    /**
     * Called in {@link LivingEntity#getVisibilityPercent(Entity)} when an entity is trying to be targeted by another
     * entity for applying a given percentage to the looking entity's original visibility range.
     *
     * @param livingEntity         the entity trying to be targeted
     * @param lookingEntity        the looking entity that is trying to target the other entity
     * @param visibilityPercentage the visibility percentage multiplied with the looking entity's targeting range
     */
    void onLivingVisibility(LivingEntity livingEntity, @Nullable Entity lookingEntity, MutableDouble visibilityPercentage);
}
