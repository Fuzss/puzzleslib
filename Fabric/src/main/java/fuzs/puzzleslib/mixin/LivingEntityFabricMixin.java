package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
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

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;
    @Unique
    private DefaultedFloat puzzleslib$fallDistance;
    @Unique
    private DefaultedFloat puzzleslib$damageMultiplier;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
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
