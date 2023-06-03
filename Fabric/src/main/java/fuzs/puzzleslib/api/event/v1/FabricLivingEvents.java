package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.api.event.v1.entity.living.*;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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
     */
    public static final Event<LivingDropsCallback> LIVING_DROPS = FabricEventFactory.createResult(LivingDropsCallback.class);
    /**
     * Called at the beginning of {@link LivingEntity#tick()}, allows cancelling ticking the entity.
     */
    public static final Event<LivingEvents.Tick> LIVING_TICK = FabricEventFactory.createResult(LivingEvents.Tick.class);
    /**
     * Called right before any reduction on damage due to e.g. armor are done, cancelling prevents any damage / armor durability being taken.
     */
    public static final Event<LivingHurtCallback> LIVING_HURT = FabricEventFactory.createResult(LivingHurtCallback.class);
    /**
     * Fired when an item starts being used in {@link LivingEntity#startUsingItem(InteractionHand)}.
     */
    public static final Event<UseItemEvents.Start> USE_ITEM_START = FabricEventFactory.createResult(UseItemEvents.Start.class);
    /**
     * Fired every tick an entity is using an item.
     */
    public static final Event<UseItemEvents.Tick> USE_ITEM_TICK = FabricEventFactory.createResult(UseItemEvents.Tick.class);
    /**
     * Fired when an item is stopped being used without being finished, meaning {@link net.minecraft.world.item.Item#getUseDuration(ItemStack)} has not been reached.
     */
    public static final Event<UseItemEvents.Stop> USE_ITEM_STOP = FabricEventFactory.createResult(UseItemEvents.Stop.class);
    /**
     * Fired when an item is finished being used, meaning {@link net.minecraft.world.item.Item#getUseDuration(ItemStack)} has run out.
     */
    public static final Event<UseItemEvents.Finish> USE_ITEM_FINISH = FabricEventFactory.create(UseItemEvents.Finish.class);
    /**
     * Called right before damage from an incoming attack is negated via blocking using a shield.
     */
    public static final Event<ShieldBlockCallback> SHIELD_BLOCK = FabricEventFactory.createResult(ShieldBlockCallback.class);
    /**
     * Fires whenever a living entity dies, allows for preventing the death.
     */
    public static final Event<LivingDeathCallback> LIVING_DEATH = FabricEventFactory.createResult(LivingDeathCallback.class);
    /**
     * Called when a child is created from breeding two parents, allows for replacing the child or for preventing any offspring from being spawned.
     */
    public static final Event<BabyEntitySpawnCallback> BABY_ENTITY_SPAWN = FabricEventFactory.createResult(BabyEntitySpawnCallback.class);
    /**
     * Called when a player is about to tame an animal, allows for preventing taming.
     */
    public static final Event<AnimalTameCallback> ANIMAL_TAME = FabricEventFactory.createResult(AnimalTameCallback.class);
    /**
     * Fires when a {@link LivingEntity} is attacked, allows for cancelling that attack.
     */
    public static final Event<LivingAttackCallback> LIVING_ATTACK = FabricEventFactory.createResult(LivingAttackCallback.class);
}
