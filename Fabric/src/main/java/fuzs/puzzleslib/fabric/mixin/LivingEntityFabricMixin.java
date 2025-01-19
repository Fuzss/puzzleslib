package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.impl.event.CapturedDropsEntity;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.EventImplHelper;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Unique
    private final ThreadLocal<ItemStack> puzzleslib$originalUseItem = new ThreadLocal<>();
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;
    @Shadow
    protected int lastHurtByPlayerTime;
    @Shadow
    @Final
    private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;
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

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo callback) {
        EventResult result = FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(LivingEntity.class.cast(this),
                damageSource
        );
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(
            method = "startUsingItem", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/LivingEntity;useItemRemaining:I",
            shift = At.Shift.AFTER
    ), cancellable = true
    )
    public void startUsingItem(InteractionHand hand, CallbackInfo callback) {
        // this injects after the field is already updated, so it is fine to use instead of ItemStack::getUseDuration
        DefaultedInt useItemRemaining = DefaultedInt.fromValue(this.useItemRemaining);
        EventResult result = FabricLivingEvents.USE_ITEM_START.invoker().onUseItemStart(LivingEntity.class.cast(this),
                this.useItem, useItemRemaining
        );
        if (result.isInterrupt()) {
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
            EventResult result = FabricLivingEvents.USE_ITEM_TICK.invoker().onUseItemTick(LivingEntity.class.cast(this),
                    usingItem, remainingUseDuration
            );
            // --this.useItemRemaining == 0 runs at the end of this method, when 0 is set increase by one again,
            // so that LivingEntity::completeUsingItem does run
            remainingUseDuration.getAsOptionalInt().ifPresent(
                    useItemRemaining -> this.useItemRemaining = useItemRemaining == 0 ? 1 : useItemRemaining);
            if (result.isInterrupt()) {
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
            target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
            shift = At.Shift.BEFORE
    )
    )
    protected void completeUsingItem(CallbackInfo callback) {
        this.puzzleslib$originalUseItem.set(this.useItem.copy());
    }

    @ModifyVariable(method = "completeUsingItem", at = @At("STORE"), ordinal = 0)
    protected ItemStack completeUsingItem(ItemStack useItem) {
        Objects.requireNonNull(this.puzzleslib$originalUseItem.get(), "use item copy is null");
        DefaultedValue<ItemStack> stack = DefaultedValue.fromValue(useItem);
        FabricLivingEvents.USE_ITEM_FINISH.invoker().onUseItemFinish(LivingEntity.class.cast(this), stack,
                this.useItemRemaining, this.puzzleslib$originalUseItem.get()
        );
        useItem = stack.getAsOptional().orElse(useItem);
        this.puzzleslib$originalUseItem.remove();
        return useItem;
    }

    @Shadow
    public abstract void stopUsingItem();

    @Inject(method = "releaseUsingItem", at = @At("HEAD"), cancellable = true)
    public void releaseUsingItem(CallbackInfo callback) {
        if (!this.useItem.isEmpty()) {
            if (FabricLivingEvents.USE_ITEM_STOP.invoker().onUseItemStop(LivingEntity.class.cast(this), this.useItem,
                    this.useItemRemaining
            ).isPass()) {
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
        if (!FabricEventImplHelper.tryOnLivingDrops(LivingEntity.class.cast(this), damageSource,
                this.lastHurtByPlayerTime
        )) {
            PuzzlesLib.LOGGER.warn("Unable to invoke LivingDropsCallback for entity {}: Drops is null",
                    this.getName().getString()
            );
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    public void die$1(DamageSource damageSource, CallbackInfo callback) {
        // this is a safety precaution, in case LivingEntity::dropAllDeathLoot does not reach TAIL and therefore doesn't spawn the captured drops (another mixin might cancel the method mid-way)
        // this should work rather fine, as LivingEntity::dropAllDeathLoot is basically exclusively called from LivingEntity::die,
        // and spawning captured drops in LivingEntity::dropAllDeathLoot only rarely has a conflict if any at all
        FabricEventImplHelper.tryOnLivingDrops(LivingEntity.class.cast(this), damageSource, this.lastHurtByPlayerTime);
    }

    @Inject(
            method = "dropExperience", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
    ), cancellable = true
    )
    protected void dropExperience(ServerLevel serverLevel, @Nullable Entity killer, CallbackInfo callback) {
        DefaultedInt experienceReward = DefaultedInt.fromValue(this.getBaseExperienceReward(serverLevel));
        EventResult result = FabricLivingEvents.EXPERIENCE_DROP.invoker().onLivingExperienceDrop(
                LivingEntity.class.cast(this), this.lastHurtByPlayer, experienceReward);
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
    protected abstract int getBaseExperienceReward(ServerLevel serverLevel);

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float damageAmount, ServerLevel serverLevel, DamageSource damageSource, @Cancellable CallbackInfo callback) {
        if (!this.isInvulnerableTo(serverLevel, damageSource)) {
            MutableBoolean cancelInjection = new MutableBoolean();
            damageAmount = FabricEventImplHelper.onLivingHurt(LivingEntity.class.cast(this), serverLevel, damageSource,
                    damageAmount, cancelInjection
            );
            if (cancelInjection.booleanValue()) callback.cancel();
        }

        return damageAmount;
    }

    @Shadow
    public abstract boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource);

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.LIVING_ATTACK.invoker().onLivingAttack(LivingEntity.class.cast(this),
                damageSource, damageAmount
        );
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @ModifyExpressionValue(
            method = "hurtServer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"
    )
    )
    public boolean hurtServer(boolean isDamageSourceBlocked, ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        return isDamageSourceBlocked && FabricLivingEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                LivingEntity.class.cast(this), damageSource, damageAmount).isPass();
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void causeFallDamage$0(float distance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        this.puzzleslib$fallDistance = DefaultedFloat.fromValue(distance);
        this.puzzleslib$damageMultiplier = DefaultedFloat.fromValue(damageMultiplier);
        if (FabricLivingEvents.LIVING_FALL.invoker().onLivingFall(LivingEntity.class.cast(this),
                this.puzzleslib$fallDistance, this.puzzleslib$damageMultiplier
        ).isInterrupt()) {
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
        if (FabricLivingEvents.LIVING_KNOCK_BACK.invoker().onLivingKnockBack(LivingEntity.class.cast(this),
                this.puzzleslib$strength, this.puzzleslib$ratioX, this.puzzleslib$ratioZ
        ).isInterrupt()) {
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

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("STORE"),
            ordinal = 1
    )
    public MobEffectInstance addEffect(@Nullable MobEffectInstance oldEffectInstance, MobEffectInstance effectInstance, @Nullable Entity entity) {
        FabricLivingEvents.MOB_EFFECT_APPLY.invoker().onMobEffectApply(LivingEntity.class.cast(this), effectInstance,
                oldEffectInstance, entity
        );
        return oldEffectInstance;
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> callback) {
        // Forge also adds this patch to spiders, but let's just say no one wants to remove poison immunity from them
        // Forge is incomplete anyway, with mobs are not affected by this event when checking for the wither effect
        EventResult result = FabricLivingEvents.MOB_EFFECT_AFFECTS.invoker().onMobEffectAffects(
                LivingEntity.class.cast(this), effectInstance);
        if (result.isInterrupt()) callback.setReturnValue(result.getAsBoolean());
    }

    @Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
    public void removeEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker().onMobEffectRemove(
                LivingEntity.class.cast(this), this.getEffect(effect));
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Inject(method = "removeAllEffects", at = @At("HEAD"))
    public void removeAllEffects(CallbackInfoReturnable<Boolean> callback) {
        if (this.level().isClientSide || this.activeEffects.isEmpty()) return;
        Map<Holder<MobEffect>, MobEffectInstance> removedActiveEffects = new HashMap<>();
        for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : this.activeEffects.entrySet()) {
            EventResult result = FabricLivingEvents.MOB_EFFECT_REMOVE.invoker().onMobEffectRemove(
                    LivingEntity.class.cast(this), entry.getValue());
            if (result.isPass()) removedActiveEffects.put(entry.getKey(), entry.getValue());
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
        FabricLivingEvents.MOB_EFFECT_EXPIRE.invoker().onMobEffectExpire(LivingEntity.class.cast(this),
                mobEffectInstance
        );
        return mobEffectInstance;
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    protected void jumpFromGround(CallbackInfo callback) {
        EventImplHelper.onLivingJump(FabricLivingEvents.LIVING_JUMP.invoker(), LivingEntity.class.cast(this));
    }

    @ModifyVariable(method = "getVisibilityPercent", at = @At(value = "TAIL", shift = At.Shift.BEFORE), ordinal = 0)
    public double getVisibilityPercent(double value, @Nullable Entity lookingEntity) {
        DefaultedDouble visibilityPercentage = DefaultedDouble.fromValue(value);
        FabricLivingEvents.LIVING_VISIBILITY.invoker().onLivingVisibility(LivingEntity.class.cast(this), lookingEntity,
                visibilityPercentage
        );
        return visibilityPercentage.getAsOptionalDouble().stream().map((double visibilityPercentageValue) -> Math.max(visibilityPercentageValue, 0.0)).findAny().orElse(value);
    }

    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack getProjectile(ItemStack projectileItemStack, ItemStack weaponItemStack) {
        if (weaponItemStack.getItem() instanceof ProjectileWeaponItem) {
            DefaultedValue<ItemStack> projectileItemStackValue = DefaultedValue.fromValue(projectileItemStack);
            FabricLivingEvents.GET_PROJECTILE.invoker().onGetProjectile(LivingEntity.class.cast(this), weaponItemStack, projectileItemStackValue);
            return projectileItemStackValue.getAsOptional().orElse(projectileItemStack);
        } else {
            return projectileItemStack;
        }
    }
}
