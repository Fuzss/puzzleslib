package fuzs.puzzleslib.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.CapturedDropsEntity;
import fuzs.puzzleslib.impl.event.LivingJumpHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Shadow
    @Final
    private Map<MobEffect, MobEffectInstance> activeEffects;
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;
    @Shadow
    protected int lastHurtByPlayerTime;
    @Unique
    private int puzzleslib$lootingLevel;
    @Unique
    protected ThreadLocal<ItemStack> puzzleslib$originalUseItem = new ThreadLocal<>();
    @Unique
    private DefaultedFloat puzzleslib$damageAmount;
    @Unique
    private float puzzleslib$hurtAmount;
    @Unique
    private DefaultedFloat puzzleslib$fallDistance;
    @Unique
    private DefaultedFloat puzzleslib$damageMultiplier;
    @Unique
    private DefaultedDouble puzzleslib$strength;
    @Unique
    private DefaultedDouble puzzleslib$ratioX;
    @Unique
    private DefaultedDouble puzzleslib$ratioZ;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo callback) {
        if (FabricLivingEvents.LIVING_TICK.invoker().onLivingTick(LivingEntity.class.cast(this)).isInterrupt()) callback.cancel();
    }

    @Inject(method = "startUsingItem", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;useItemRemaining:I", shift = At.Shift.AFTER), cancellable = true)
    public void startUsingItem(InteractionHand hand, CallbackInfo callback) {
        DefaultedInt useItemRemaining = DefaultedInt.fromValue(this.useItemRemaining);
        EventResult result = FabricLivingEvents.USE_ITEM_START.invoker().onUseItemStart(LivingEntity.class.cast(this), this.useItem, useItemRemaining);
        if (result.isInterrupt()) {
            this.useItem = ItemStack.EMPTY;
            this.useItemRemaining = 0;
            callback.cancel();
        } else {
            this.useItemRemaining = useItemRemaining.getAsOptionalInt().orElse(this.useItemRemaining);
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    protected void updateUsingItem(ItemStack useItem, CallbackInfo callback) {
        // moved here from LivingEntity#updatingUsingItem on Forge, just a little more simple to implement
        DefaultedInt useItemRemaining = DefaultedInt.fromValue(this.useItemRemaining);
        EventResult result = FabricLivingEvents.USE_ITEM_TICK.invoker().onUseItemTick(LivingEntity.class.cast(this), useItem, useItemRemaining);
        this.useItemRemaining = result.isInterrupt() ? 0 : useItemRemaining.getAsOptionalInt().orElse(this.useItemRemaining);
        // --this.useItemRemaining == 0 runs at the end of this method, since we will be below that LivingEntity#completeUsingItem does not run (intentionally)
    }

    @Shadow
    private void updatingUsingItem() {
        throw new RuntimeException();
    }

    @Inject(method = "completeUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BEFORE))
    protected void completeUsingItem(CallbackInfo callback) {
        this.puzzleslib$originalUseItem.set(this.useItem.copy());
    }

    @ModifyVariable(method = "completeUsingItem", at = @At("STORE"), ordinal = 0)
    protected ItemStack completeUsingItem(ItemStack useItem) {
        Objects.requireNonNull(this.puzzleslib$originalUseItem.get(), "use item copy is null");
        DefaultedValue<ItemStack> stack = DefaultedValue.fromValue(useItem);
        FabricLivingEvents.USE_ITEM_FINISH.invoker().onUseItemFinish(LivingEntity.class.cast(this), stack, this.useItemRemaining, this.puzzleslib$originalUseItem.get());
        useItem = stack.getAsOptional().orElse(useItem);
        this.puzzleslib$originalUseItem.remove();
        return useItem;
    }

    @Shadow
    public abstract void stopUsingItem();

    @Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
    public void releaseUsingItem(CallbackInfo callback) {
        if (!this.useItem.isEmpty()) {
            if (FabricLivingEvents.USE_ITEM_STOP.invoker().onUseItemStop(LivingEntity.class.cast(this), this.useItem, this.useItemRemaining).isPass()) return;
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
            this.stopUsingItem();
            callback.cancel();
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    protected void dropAllDeathLoot$0(DamageSource damageSource, CallbackInfo callback) {
        this.puzzleslib$lootingLevel = 0;
        ((CapturedDropsEntity) this).puzzleslib$acceptCapturedDrops(Lists.newArrayList());
    }

    @ModifyVariable(method = "dropAllDeathLoot", at = @At("STORE"), ordinal = 0)
    protected int dropAllDeathLoot$1(int lootingLevel, DamageSource damageSource) {
        MutableInt mutableLootingLevel = MutableInt.fromValue(lootingLevel);
        FabricLivingEvents.LOOTING_LEVEL.invoker().onLootingLevel(LivingEntity.class.cast(this), damageSource, mutableLootingLevel);
        // we do not have access to the local lootingLevel variable at TAIL later where it is needed for invoking LivingDropsCallback
        // (the local capture seems to fail due to the way the local lootingLevel variable is initialised)
        // so instead capture the value after our LootingLevelCallback has run, potentially missing out on changes applied by other mixins
        return this.puzzleslib$lootingLevel = mutableLootingLevel.getAsInt();
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    protected void dropAllDeathLoot$2(DamageSource damageSource, CallbackInfo callback) {
        Collection<ItemEntity> capturedDrops = ((CapturedDropsEntity) this).puzzleslib$acceptCapturedDrops(null);
        if (capturedDrops != null) {
            EventResult result = FabricLivingEvents.LIVING_DROPS.invoker().onLivingDrops(LivingEntity.class.cast(this), damageSource, capturedDrops, this.puzzleslib$lootingLevel, this.lastHurtByPlayerTime > 0);
            if (result.isPass()) capturedDrops.forEach(itemEntity -> this.level().addFreshEntity(itemEntity));
        } else {
            PuzzlesLib.LOGGER.warn("Unable to invoke LivingDropsCallback for entity {}: Drops is null", this.getName().getString());
        }
    }

    @Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"), cancellable = true)
    protected void dropExperience(CallbackInfo callback) {
        DefaultedInt experienceReward = DefaultedInt.fromValue(this.getExperienceReward());
        EventResult result = FabricLivingEvents.EXPERIENCE_DROP.invoker().onLivingExperienceDrop(LivingEntity.class.cast(this), this.lastHurtByPlayer, experienceReward);
        if (result.isInterrupt()) {
            callback.cancel();
        } else {
            experienceReward.getAsOptionalInt().ifPresent(value -> {
                ExperienceOrb.award((ServerLevel) this.level(), this.position(), value);
                callback.cancel();
            });
        }
    }

    @Shadow
    protected abstract int getExperienceReward();

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    protected void actuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo callback) {
        if (!this.isInvulnerableTo(damageSource)) {
            this.puzzleslib$damageAmount = DefaultedFloat.fromValue(damageAmount);
            if (FabricLivingEvents.LIVING_HURT.invoker().onLivingHurt(LivingEntity.class.cast(this), damageSource, this.puzzleslib$damageAmount).isInterrupt()) {
                callback.cancel();
            }
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float damageAmount, DamageSource damageSource) {
        if (!this.isInvulnerableTo(damageSource)) {
            Objects.requireNonNull(this.puzzleslib$damageAmount, "damage amount is null");
            damageAmount = this.puzzleslib$damageAmount.getAsOptionalFloat().orElse(damageAmount);
            this.puzzleslib$damageAmount = null;
        }
        return damageAmount;
    }

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @ModifyVariable(method = "hurt", at = @At(value = "LOAD", ordinal = 1), ordinal = 0)
    public float hurt$0(float amount, DamageSource source) {
        // hook in before any blocking checks are done, there is no good way to cancel the block after this
        // check everything again, it shouldn't affect anything
        if (amount > 0.0F && this.isDamageSourceBlocked(source)) {
            this.puzzleslib$hurtAmount = amount;
            DefaultedFloat blockedDamage = DefaultedFloat.fromValue(amount);
            EventResult result = FabricLivingEvents.SHIELD_BLOCK.invoker().onShieldBlock(LivingEntity.class.cast(this), source, blockedDamage);
            // prevent vanilla shield logic from running when the callback was cancelled, the original damage amount is restored from puzzleslib$hurtAmount later
            if (result.isInterrupt()) return 0.0F;
            // reduce by blocked amount
            this.puzzleslib$hurtAmount -= blockedDamage.getAsFloat();
            // return the amount blocked by the shield
            return blockedDamage.getAsFloat();
        }
        return amount;
    }

    @ModifyVariable(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;walkAnimation:Lnet/minecraft/world/entity/WalkAnimationState;", shift = At.Shift.BEFORE), ordinal = 0)
    public float hurt$1(float amount, DamageSource source) {
        // this is only present if the damage source could be blocked
        if (this.puzzleslib$hurtAmount != 0.0F) {
            // restore original amount when the shield blocking callback was cancelled
            amount = this.puzzleslib$hurtAmount;
            this.puzzleslib$hurtAmount = 0.0F;
        }
        return amount;
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void causeFallDamage$0(float distance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        this.puzzleslib$fallDistance = DefaultedFloat.fromValue(distance);
        this.puzzleslib$damageMultiplier = DefaultedFloat.fromValue(damageMultiplier);
        if (FabricLivingEvents.LIVING_FALL.invoker().onLivingFall(LivingEntity.class.cast(this), this.puzzleslib$fallDistance, this.puzzleslib$damageMultiplier).isInterrupt()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public float causeFallDamage$1(float fallDistance) {
        Objects.requireNonNull(this.puzzleslib$fallDistance, "fall distance is null");
        fallDistance = this.puzzleslib$fallDistance.getAsOptionalFloat().orElse(fallDistance);
        this.puzzleslib$fallDistance = null;
        return fallDistance;
    }

    @ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    public float causeFallDamage$2(float damageMultiplier) {
        Objects.requireNonNull(this.puzzleslib$damageMultiplier, "damage multiplier is null");
        damageMultiplier = this.puzzleslib$damageMultiplier.getAsOptionalFloat().orElse(damageMultiplier);
        this.puzzleslib$damageMultiplier = null;
        return damageMultiplier;
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    public void knockback$0(double strength, double ratioX, double ratioZ, CallbackInfo callback) {
        this.puzzleslib$strength = DefaultedDouble.fromValue(strength);
        this.puzzleslib$ratioX = DefaultedDouble.fromValue(ratioX);
        this.puzzleslib$ratioZ = DefaultedDouble.fromValue(ratioZ);
        if (FabricLivingEvents.LIVING_KNOCK_BACK.invoker().onLivingKnockBack(LivingEntity.class.cast(this), this.puzzleslib$strength, this.puzzleslib$ratioX, this.puzzleslib$ratioZ).isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(method = "knockback", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public double knockback$1(double strength) {
        Objects.requireNonNull(this.puzzleslib$strength, "strength is null");
        strength = this.puzzleslib$strength.getAsOptionalDouble().orElse(strength);
        this.puzzleslib$strength = null;
        return strength;
    }

    @ModifyVariable(method = "knockback", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    public double knockback$2(double ratioX) {
        Objects.requireNonNull(this.puzzleslib$ratioX, "ratio x is null");
        ratioX = this.puzzleslib$ratioX.getAsOptionalDouble().orElse(ratioX);
        this.puzzleslib$ratioX = null;
        return ratioX;
    }

    @ModifyVariable(method = "knockback", at = @At(value = "HEAD"), ordinal = 2, argsOnly = true)
    public double knockback$3(double ratioZ) {
        Objects.requireNonNull(this.puzzleslib$ratioZ, "ratio z is null");
        ratioZ = this.puzzleslib$ratioZ.getAsOptionalDouble().orElse(ratioZ);
        this.puzzleslib$ratioZ = null;
        return ratioZ;
    }

    @ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("STORE"), ordinal = 1)
    public MobEffectInstance addEffect(@Nullable MobEffectInstance oldEffectInstance, MobEffectInstance effectInstance, @Nullable Entity entity) {
        FabricLivingEvents.MOB_EFFECT_APPLY.invoker().onMobEffectApply(LivingEntity.class.cast(this), effectInstance, oldEffectInstance, entity);
        return oldEffectInstance;
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> callback) {
        // Forge also adds this patch to spiders, but let's just say no one wants to remove poison immunity from them
        // Forge is incomplete anyway, with mobs are not affected by this event when checking for the wither effect
        EventResult result = FabricLivingEvents.MOB_EFFECT_AFFECTS.invoker().onMobEffectAffects(LivingEntity.class.cast(this), effectInstance);
        if (result.isInterrupt()) callback.setReturnValue(result.getAsBoolean());
    }

    @Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
    public void removeEffect(MobEffect effect, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker().onMobEffectRemove(LivingEntity.class.cast(this), this.getEffect(effect));
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(MobEffect effect);

    @Inject(method = "removeEffect", at = @At("HEAD"))
    public void removeAllEffects(CallbackInfoReturnable<Boolean> callback) {
        if (this.level().isClientSide) return;
        Set<MobEffectInstance> activeEffectsToKeep = Sets.newIdentityHashSet();
        for (MobEffectInstance mobEffectInstance : this.activeEffects.values()) {
            EventResult result = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker().onMobEffectRemove(LivingEntity.class.cast(this), mobEffectInstance);
            if (result.isInterrupt()) activeEffectsToKeep.add(mobEffectInstance);
        }
        if (!activeEffectsToKeep.isEmpty()) {
            Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();
            boolean flag;
            for(flag = false; iterator.hasNext(); flag = true) {
                MobEffectInstance effect = iterator.next();
                this.onEffectRemoved(effect);
                iterator.remove();
            }
            callback.setReturnValue(flag);
        }
    }

    @Shadow
    protected abstract void onEffectRemoved(MobEffectInstance effectInstance);

    @ModifyVariable(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"), ordinal = 0)
    protected MobEffectInstance tickEffects(MobEffectInstance mobEffectInstance) {
        FabricLivingEvents.MOB_EFFECT_EXPIRE.invoker().onMobEffectExpire(LivingEntity.class.cast(this), mobEffectInstance);
        return mobEffectInstance;
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    protected void jumpFromGround(CallbackInfo callback) {
        LivingJumpHelper.onLivingJump(FabricLivingEvents.LIVING_JUMP.invoker(), LivingEntity.class.cast(this));
    }
}
