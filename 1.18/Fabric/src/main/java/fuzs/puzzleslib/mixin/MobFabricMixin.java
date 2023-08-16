package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity {
    @Shadow
    @Nullable
    private LivingEntity target;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), ordinal = 0)
    public LivingEntity setTarget(@Nullable LivingEntity entity) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(entity);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, target);
        return result.isInterrupt() ? this.target : target.getAsOptional().orElse(entity);
    }

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;", shift = At.Shift.BEFORE), cancellable = true)
    public void checkDespawn(CallbackInfo callback) {
        EventResult result = FabricLivingEvents.CHECK_MOB_DESPAWN.invoker().onCheckMobDespawn(Mob.class.cast(this), (ServerLevel) this.level);
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
