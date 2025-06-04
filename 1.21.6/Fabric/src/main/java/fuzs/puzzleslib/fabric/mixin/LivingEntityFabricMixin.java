package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.*;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.impl.event.CapturedDropsEntity;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import fuzs.puzzleslib.impl.event.data.DefaultedDouble;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import fuzs.puzzleslib.impl.event.data.DefaultedInt;
import fuzs.puzzleslib.impl.event.data.DefaultedValue;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;
    @Shadow
    protected int lastHurtByPlayerMemoryTime;
    @Shadow
    @Final
    private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo callback) {
        EventResult eventResult = FabricLivingEvents.LIVING_DEATH.invoker()
                .onLivingDeath(LivingEntity.class.cast(this), damageSource);
        if (eventResult.isInterrupt()) callback.cancel();
    }

    @Inject(
            method = "startUsingItem", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/LivingEntity;useItemRemaining:I",
            shift = At.Shift.AFTER
    ), cancellable = true
    )
    public void startUsingItem(InteractionHand interactionHand, CallbackInfo callback) {
        // this injects after the field is already updated, so it is fine to use instead of ItemStack::getUseDuration
        DefaultedInt useItemRemaining = DefaultedInt.fromValue(this.useItemRemaining);
        EventResult eventResult = FabricLivingEvents.USE_ITEM_START.invoker()
                .onUseItemStart(LivingEntity.class.cast(this), this.useItem, useItemRemaining);
        if (eventResult.isInterrupt()) {
            this.useItem = ItemStack.EMPTY;
            this.useItemRemaining = 0;
            callback.cancel();
        } else {
            this.useItemRemaining = useItemRemaining.getAsOptionalInt().orElse(this.useItemRemaining);
        }
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"), cancellable = true)
    protected void updateUsingItem(ItemStack usingItem, CallbackInfo callback) {
        if (!usingItem.isEmpty()) {
            DefaultedInt remainingUseDuration = DefaultedInt.fromValue(this.getUseItemRemainingTicks());
            EventResult eventResult = FabricLivingEvents.USE_ITEM_TICK.invoker()
                    .onUseItemTick(LivingEntity.class.cast(this), usingItem, remainingUseDuration);
            // --this.useItemRemaining == 0 runs at the end of this method, when 0 is set increase by one again,
            // so that LivingEntity::completeUsingItem does run
            remainingUseDuration.getAsOptionalInt()
                    .ifPresent(useItemRemaining -> this.useItemRemaining =
                            useItemRemaining == 0 ? 1 : useItemRemaining);
            if (eventResult.isInterrupt()) {
                // this copies LivingEntity::updateUsingItem without calling ItemStack::onUseTick
                if (--this.useItemRemaining == 0 && !this.level().isClientSide && !usingItem.useOnRelease()) {
                    this.completeUsingItem();
                }
                callback.cancel();
            }
        }
    }

    @Shadow
    public abstract int getUseItemRemainingTicks();

    @Shadow
    protected abstract void completeUsingItem();

    @Shadow
    private void updatingUsingItem() {
        throw new RuntimeException();
    }

    @Inject(
            method = "completeUsingItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
    )
    )
    protected void completeUsingItem(CallbackInfo callback, @Share("originalUseItem") LocalRef<ItemStack> originalUseItem) {
        originalUseItem.set(this.useItem.copy());
    }

    @ModifyVariable(method = "completeUsingItem", at = @At("STORE"), ordinal = 0)
    protected ItemStack completeUsingItem(ItemStack useItem, @Share("originalUseItem") LocalRef<ItemStack> originalUseItem) {
        Objects.requireNonNull(originalUseItem.get(), "original use item is null");
        DefaultedValue<ItemStack> itemStack = DefaultedValue.fromValue(useItem);
        FabricLivingEvents.USE_ITEM_FINISH.invoker()
                .onUseItemFinish(LivingEntity.class.cast(this), itemStack, originalUseItem.get());
        return itemStack.getAsOptional().orElse(useItem);
    }

    @Shadow
    public abstract void stopUsingItem();

    @Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
    public void releaseUsingItem(CallbackInfo callback) {
        if (!this.useItem.isEmpty()) {
            if (FabricLivingEvents.USE_ITEM_STOP.invoker()
                    .onUseItemStop(LivingEntity.class.cast(this), this.useItem, this.useItemRemaining)
                    .isPass()) {
                return;
            }
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
            this.stopUsingItem();
            callback.cancel();
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    protected void dropAllDeathLoot$1(ServerLevel level, DamageSource damageSource, CallbackInfo callback) {
        ((CapturedDropsEntity) this).puzzleslib$acceptCapturedDrops(new ArrayList<>());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    protected void dropAllDeathLoot$2(ServerLevel level, DamageSource damageSource, CallbackInfo callback) {
        if (!FabricEventImplHelper.tryOnLivingDrops(LivingEntity.class.cast(this),
                damageSource,
                this.lastHurtByPlayerMemoryTime)) {
            PuzzlesLib.LOGGER.warn("Unable to invoke LivingDropsCallback for entity {}: Drops is null",
                    this.getName().getString());
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    public void die$1(DamageSource damageSource, CallbackInfo callback) {
        // this is a safety precaution, in case LivingEntity::dropAllDeathLoot does not reach TAIL and therefore doesn't spawn the captured drops (another mixin might cancel the method mid-way)
        // this should work rather fine, as LivingEntity::dropAllDeathLoot is basically exclusively called from LivingEntity::die,
        // and spawning captured drops in LivingEntity::dropAllDeathLoot only rarely has a conflict if any at all
        FabricEventImplHelper.tryOnLivingDrops(LivingEntity.class.cast(this),
                damageSource,
                this.lastHurtByPlayerMemoryTime);
    }

    @Inject(
            method = "dropExperience", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
    ), cancellable = true
    )
    protected void dropExperience(ServerLevel serverLevel, @Nullable Entity killer, CallbackInfo callback) {
        DefaultedInt experienceReward = DefaultedInt.fromValue(this.getBaseExperienceReward(serverLevel));
        EventResult eventResult = FabricLivingEvents.EXPERIENCE_DROP.invoker()
                .onLivingExperienceDrop(LivingEntity.class.cast(this), this.getLastHurtByPlayer(), experienceReward);
        if (eventResult.isInterrupt()) {
            callback.cancel();
        } else {
            experienceReward.getAsOptionalInt().ifPresent(value -> {
                ExperienceOrb.award((ServerLevel) this.level(), this.position(), value);
                callback.cancel();
            });
        }
    }

    @Shadow
    protected abstract int getBaseExperienceReward(ServerLevel serverLevel);

    @Shadow
    public abstract @Nullable Player getLastHurtByPlayer();

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float damageAmount, ServerLevel serverLevel, DamageSource damageSource, @Cancellable CallbackInfo callback) {
        if (!this.isInvulnerableTo(serverLevel, damageSource)) {
            MutableBoolean cancelInjection = new MutableBoolean();
            damageAmount = FabricEventImplHelper.onLivingHurt(LivingEntity.class.cast(this),
                    serverLevel,
                    damageSource,
                    damageAmount,
                    cancelInjection);
            if (cancelInjection.booleanValue()) callback.cancel();
        }

        return damageAmount;
    }

    @Shadow
    public abstract boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource);

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Boolean> callback) {
        EventResult eventResult = FabricLivingEvents.LIVING_ATTACK.invoker()
                .onLivingAttack(LivingEntity.class.cast(this), damageSource, damageAmount);
        if (eventResult.isInterrupt()) callback.setReturnValue(false);
    }

    @ModifyExpressionValue(
            method = "applyItemBlocking", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/component/BlocksAttacks;resolveBlockedDamage(Lnet/minecraft/world/damagesource/DamageSource;FD)F"
    )
    )
    public float applyItemBlocking(float blockedDamage, ServerLevel serverLevel, DamageSource damageSource, float damageAmount, @Cancellable CallbackInfoReturnable<Float> callback) {
        DefaultedFloat blockedDamageValue = DefaultedFloat.fromValue(blockedDamage);
        EventResult eventResult = FabricLivingEvents.SHIELD_BLOCK.invoker()
                .onShieldBlock(LivingEntity.class.cast(this), damageSource, blockedDamageValue);
        if (eventResult.isInterrupt()) {
            callback.setReturnValue(0.0F);
            return 0.0F;
        } else {
            return blockedDamageValue.getAsFloat();
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void causeFallDamage(CallbackInfoReturnable<Boolean> callback, @Local(
            ordinal = 0, argsOnly = true
    ) LocalDoubleRef fallDistanceRef, @Local(
            ordinal = 0, argsOnly = true
    ) LocalFloatRef damageMultiplierRef) {
        MutableDouble fallDistance = MutableDouble.fromEvent(fallDistanceRef::set, fallDistanceRef::get);
        MutableFloat damageMultiplier = MutableFloat.fromEvent(damageMultiplierRef::set, damageMultiplierRef::get);
        EventResult eventResult = FabricLivingEvents.LIVING_FALL.invoker()
                .onLivingFall(LivingEntity.class.cast(this), fallDistance, damageMultiplier);
        if (eventResult.isInterrupt()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    public void knockback(CallbackInfo callback, @Local(
            ordinal = 0, argsOnly = true
    ) LocalDoubleRef strengthRef, @Local(ordinal = 1, argsOnly = true) LocalDoubleRef ratioXRef, @Local(
            ordinal = 2, argsOnly = true
    ) LocalDoubleRef ratioZRef) {
        MutableDouble knockbackStrength = MutableDouble.fromEvent(strengthRef::set, strengthRef::get);
        MutableDouble ratioX = MutableDouble.fromEvent(ratioXRef::set, ratioXRef::get);
        MutableDouble ratioZ = MutableDouble.fromEvent(ratioZRef::set, ratioZRef::get);
        EventResult eventResult = FabricLivingEvents.LIVING_KNOCK_BACK.invoker()
                .onLivingKnockBack(LivingEntity.class.cast(this), knockbackStrength, ratioX, ratioZ);
        if (eventResult.isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("STORE"),
            ordinal = 1
    )
    public MobEffectInstance addEffect(@Nullable MobEffectInstance oldEffectInstance, MobEffectInstance mobEffect, @Nullable Entity entity) {
        FabricLivingEvents.MOB_EFFECT_APPLY.invoker()
                .onMobEffectApply(LivingEntity.class.cast(this), mobEffect, oldEffectInstance, entity);
        return oldEffectInstance;
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance mobEffect, CallbackInfoReturnable<Boolean> callback) {
        // Forge also adds this patch to spiders, but let's just say no one wants to remove poison immunity from them
        // Forge is incomplete anyway, with mobs are not affected by this event when checking for the wither effect
        EventResult eventResult = FabricLivingEvents.MOB_EFFECT_AFFECTS.invoker()
                .onMobEffectAffects(LivingEntity.class.cast(this), mobEffect);
        if (eventResult.isInterrupt()) callback.setReturnValue(eventResult.getAsBoolean());
    }

    @Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
    public void removeEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> callback) {
        EventResult eventResult = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker()
                .onMobEffectRemove(LivingEntity.class.cast(this), this.getEffect(effect));
        if (eventResult.isInterrupt()) callback.setReturnValue(false);
    }

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Inject(method = "removeAllEffects", at = @At("HEAD"))
    public void removeAllEffects(CallbackInfoReturnable<Boolean> callback) {
        if (this.level().isClientSide || this.activeEffects.isEmpty()) return;
        Map<Holder<MobEffect>, MobEffectInstance> removedActiveEffects = new HashMap<>();
        for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : this.activeEffects.entrySet()) {
            EventResult eventResult = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker()
                    .onMobEffectRemove(LivingEntity.class.cast(this), entry.getValue());
            if (eventResult.isPass()) removedActiveEffects.put(entry.getKey(), entry.getValue());
        }
        if (removedActiveEffects.size() != this.activeEffects.size()) {
            removedActiveEffects.keySet().forEach(this.activeEffects::remove);
            this.onEffectsRemoved(removedActiveEffects.values());
            callback.setReturnValue(true);
        }
    }

    @Shadow
    protected abstract void onEffectsRemoved(Collection<MobEffectInstance> mobEffects);

    @ModifyVariable(
            method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"), ordinal = 0
    )
    protected MobEffectInstance tickEffects(MobEffectInstance mobEffectInstance) {
        FabricLivingEvents.MOB_EFFECT_EXPIRE.invoker()
                .onMobEffectExpire(LivingEntity.class.cast(this), mobEffectInstance);
        return mobEffectInstance;
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    protected void jumpFromGround(CallbackInfo callback) {
        EventImplHelper.onLivingJump(FabricLivingEvents.LIVING_JUMP.invoker(), LivingEntity.class.cast(this));
    }

    @ModifyVariable(method = "getVisibilityPercent", at = @At(value = "TAIL", shift = At.Shift.BEFORE), ordinal = 0)
    public double getVisibilityPercent(double value, @Nullable Entity lookingEntity) {
        DefaultedDouble visibilityPercentage = DefaultedDouble.fromValue(value);
        FabricLivingEvents.LIVING_VISIBILITY.invoker()
                .onLivingVisibility(LivingEntity.class.cast(this), lookingEntity, visibilityPercentage);
        return visibilityPercentage.getAsOptionalDouble()
                .stream()
                .map((double visibilityPercentageValue) -> Math.max(visibilityPercentageValue, 0.0))
                .findAny()
                .orElse(value);
    }

    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack getProjectile(ItemStack projectileItemStack, ItemStack weaponItemStack) {
        if (weaponItemStack.getItem() instanceof ProjectileWeaponItem) {
            DefaultedValue<ItemStack> projectileItemStackValue = DefaultedValue.fromValue(projectileItemStack);
            FabricLivingEvents.PICK_PROJECTILE.invoker()
                    .onPickProjectile(LivingEntity.class.cast(this), weaponItemStack, projectileItemStackValue);
            return projectileItemStackValue.getAsOptional().orElse(projectileItemStack);
        } else {
            return projectileItemStack;
        }
    }
}
