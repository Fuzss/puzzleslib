package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.impl.event.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity {
    @Shadow
    @Nullable
    private LivingEntity target;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    public void setTarget(@Nullable LivingEntity target, CallbackInfo callback) {
        DefaultedValue<LivingEntity> targetValue = DefaultedValue.fromValue(target);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, targetValue);
        if (result.isInterrupt()) {
            callback.cancel();
        } else if (targetValue.getAsOptional().isPresent()) {
            this.target = targetValue.get();
            callback.cancel();
        }
    }

    @Inject(method = "checkDespawn",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"),
            cancellable = true)
    public void checkDespawn(CallbackInfo callback) {
        EventResult result = FabricLivingEvents.CHECK_MOB_DESPAWN.invoker()
                .onCheckMobDespawn(Mob.class.cast(this), (ServerLevel) this.level());
        if (result.isInterrupt()) {
            if (result.getAsBoolean()) {
                this.discard();
            } else {
                this.noActionTime = 0;
            }

            callback.cancel();
        }
    }
}
