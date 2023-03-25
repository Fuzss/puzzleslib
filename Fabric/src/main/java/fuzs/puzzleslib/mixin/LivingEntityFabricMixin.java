package fuzs.puzzleslib.mixin;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.CapturedDropsEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;
    @Shadow
    protected int lastHurtByPlayerTime;
    @Unique
    private int puzzleslib$lootingLevel;
    @Unique
    private DefaultedFloat puzzleslib$fallDistance;
    @Unique
    private DefaultedFloat puzzleslib$damageMultiplier;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo callback) {
        if (FabricLivingEvents.LIVING_TICK.invoker().onLivingTick(LivingEntity.class.cast(this)).isInterrupt()) callback.cancel();
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
            if (result.isPass()) capturedDrops.forEach(itemEntity -> this.level.addFreshEntity(itemEntity));
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
                ExperienceOrb.award((ServerLevel) this.level, this.position(), value);
                callback.cancel();
            });
        }
    }

    @Shadow
    protected abstract int getExperienceReward();

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void causeFallDamage$0(float distance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        this.puzzleslib$fallDistance = DefaultedFloat.fromValue(distance);
        this.puzzleslib$damageMultiplier = DefaultedFloat.fromValue(damageMultiplier);
        if (FabricLivingEvents.LIVING_FALL.invoker().onLivingFall(LivingEntity.class.cast(this), this.puzzleslib$fallDistance, this.puzzleslib$damageMultiplier).isInterrupt()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD"), ordinal = 0)
    public float causeFallDamage$1(float fallDistance) {
        return this.puzzleslib$fallDistance.getAsOptionalFloat().orElse(fallDistance);
    }

    @ModifyVariable(method = "causeFallDamage", at = @At(value = "HEAD"), ordinal = 1)
    public float causeFallDamage$2(float damageMultiplier) {
        return this.puzzleslib$damageMultiplier.getAsOptionalFloat().orElse(damageMultiplier);
    }
}
