package fuzs.puzzleslib.fabric.api.event.v1;

import fuzs.puzzleslib.api.event.v1.entity.living.*;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
    public static final Event<LivingTickCallback> LIVING_TICK = FabricEventFactory.createResult(LivingTickCallback.class);
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
    /**
     * Called before an entity is knocked-back in {@link LivingEntity#knockback(double, double, double)}, allows for preventing the knock-back.
     */
    public static final Event<LivingKnockBackCallback> LIVING_KNOCK_BACK = FabricEventFactory.createResult(LivingKnockBackCallback.class);
    /**
     * Called when the game checks whether a new {@link MobEffectInstance} can be applied to a {@link LivingEntity} in {@link LivingEntity#canBeAffected(MobEffectInstance)}.
     */
    public static final Event<MobEffectEvents.Affects> MOB_EFFECT_AFFECTS = FabricEventFactory.createResult(MobEffectEvents.Affects.class);
    /**
     * Called when a new {@link MobEffectInstance} is added to a {@link LivingEntity} in {@link LivingEntity#addEffect(MobEffectInstance, Entity)}.
     */
    public static final Event<MobEffectEvents.Apply> MOB_EFFECT_APPLY = FabricEventFactory.create(MobEffectEvents.Apply.class);
    /**
     * Called when a {@link MobEffectInstance} is removed from a {@link LivingEntity} in {@link LivingEntity#removeEffect(MobEffect)}.
     */
    public static final Event<MobEffectEvents.Remove> MOB_EFFECT_REMOVE = FabricEventFactory.createResult(MobEffectEvents.Remove.class);
    /**
     * Called when a {@link MobEffectInstance} is removed from a {@link LivingEntity} in <code>net.minecraft.world.entity.LivingEntity#tickEffects</code> due to the instance duration having run out.
     */
    public static final Event<MobEffectEvents.Expire> MOB_EFFECT_EXPIRE = FabricEventFactory.create(MobEffectEvents.Expire.class);
    /**
     * Called when an entity is jumping, allows for modifying the jump height as well as preventing the jump.
     */
    public static final Event<LivingJumpCallback> LIVING_JUMP = FabricEventFactory.createResult(LivingJumpCallback.class);
    /**
     * Called in {@link LivingEntity#getVisibilityPercent(Entity)} when an entity is trying to be targeted by another entity for applying a given percentage to the looking entity's original visibility range.
     */
    public static final Event<LivingVisibilityCallback> LIVING_VISIBILITY = FabricEventFactory.create(LivingVisibilityCallback.class);
    /**
     * Called when a {@link Mob} sets a new target.
     */
    public static final Event<LivingChangeTargetCallback> LIVING_CHANGE_TARGET = FabricEventFactory.createResult(LivingChangeTargetCallback.class);
    /**
     * Fires inside of {@link Mob#checkDespawn()} to help determine if the {@link Mob} should despawn.
     */
    public static final Event<CheckMobDespawnCallback> CHECK_MOB_DESPAWN = FabricEventFactory.createResult(CheckMobDespawnCallback.class);
    /**
     * Runs when the game updates an entity's air supply depending on if the entity can currently breathe or not.
     */
    public static final Event<LivingBreathEvents.Breathe> LIVING_BREATHE = FabricEventFactory.createResult(LivingBreathEvents.Breathe.class);
    /**
     * Runs before the game checks if an entity that is submerged should be damaged from drowning.
     */
    public static final Event<LivingBreathEvents.Drown> LIVING_DROWN = FabricEventFactory.createResult(LivingBreathEvents.Drown.class);
    /**
     * Fires whenever equipment changes are detected on an entity in {@link LivingEntity#collectEquipmentChanges()} from {@link LivingEntity#tick()}.
     */
    public static final Event<LivingEquipmentChangeCallback> LIVING_EQUIPMENT_CHANGE = FabricEventFactory.create(LivingEquipmentChangeCallback.class);

    private FabricLivingEvents() {

    }
}
