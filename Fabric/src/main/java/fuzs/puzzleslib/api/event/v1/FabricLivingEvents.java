package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.damagesource.DamageSource;
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
    /**
     * Called just before a {@link LivingEntity} drops all its loot for determining the level
     * of {@link net.minecraft.world.item.enchantment.Enchantments#MOB_LOOTING} that should be applied to the drops.
     */
    public static final Event<LootingLevelCallback> LOOTING_LEVEL = FabricEventFactory.create(LootingLevelCallback.class);
    /**
     * Called right before drops from a killed entity are spawned in the world.
     * <p>This event is fired whenever an Entity dies and drops items in {@link LivingEntity#die(DamageSource)}.
     * <p><code>drops</code> can be modified to change the loot that is dropped.
     */
    public static final Event<LivingDropsCallback> LIVING_DROPS = FabricEventFactory.createResult(LivingDropsCallback.class);
    /**
     * Called at the beginning of {@link LivingEntity#tick()}, allows cancelling ticking the entity.
     */
    public static final Event<LivingEvents.Tick> LIVING_TICK = FabricEventFactory.createResult(LivingEvents.Tick.class);
}
