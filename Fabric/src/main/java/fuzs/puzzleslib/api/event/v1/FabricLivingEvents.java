package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingExperienceDropCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingFallCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.entity.LivingEntity;

/**
 * Events originally found on Forge in the <code>net.minecraftforge.event.entity.living</code> package.
 */
public final class FabricLivingEvents {
    /**
     * Called right before xp drops are spawned in the world. Allows for cancelling drops or changing the amount.
     */
    public static final Event<LivingExperienceDropCallback> EXPERIENCE_DROP = FabricEventFactory.createResult(LivingExperienceDropCallback.class);
    /**
     * Called right at the beginning of {@link LivingEntity#causeFallDamage}, allows the method to be cancelled to prevent fall damage.
     * <p>Can also be used to modify the distance the entity has fallen used for damage calculation, as well as the damage multiplier defined by the block the entity is falling on to.
     */
    public static final Event<LivingFallCallback> LIVING_FALL = FabricEventFactory.createResult(LivingFallCallback.class);
}
