package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.impl.event.SpawnTypeMob;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity implements SpawnTypeMob {
    @Shadow
    @Nullable
    private LivingEntity target;
    @Unique
    @Nullable
    private EntitySpawnReason puzzleslib$spawnType;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData, CallbackInfoReturnable<SpawnGroupData> callback) {
        this.puzzleslib$spawnType = reason;
    }

    @Override
    @Nullable
    public final EntitySpawnReason puzzleslib$getSpawnType() {
        return this.puzzleslib$spawnType;
    }

    @Override
    public void puzzleslib$setSpawnType(@Nullable EntitySpawnReason mobSpawnType) {
        this.puzzleslib$spawnType = mobSpawnType;
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), ordinal = 0)
    public LivingEntity setTarget(@Nullable LivingEntity entity) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(entity);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, target);
        return result.isInterrupt() ? this.target : target.getAsOptional().orElse(entity);
    }

    @Inject(
            method = "checkDespawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"
            ),
            cancellable = true
    )
    public void checkDespawn(CallbackInfo callback) {
        EventResult result = FabricLivingEvents.CHECK_MOB_DESPAWN.invoker().onCheckMobDespawn(Mob.class.cast(this),
                (ServerLevel) this.level()
        );
        if (result.isInterrupt()) {
            if (result.getAsBoolean()) {
                this.discard();
            } else {
                this.noActionTime = 0;
            }
            callback.cancel();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        if (this.puzzleslib$spawnType != null) {
            String key = PuzzlesLibMod.id("spawn_type").toString();
            compound.putString(key, this.puzzleslib$spawnType.name());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        String key = PuzzlesLibMod.id("spawn_type").toString();
        if (compound.contains(key)) {
            try {
                this.puzzleslib$spawnType = EntitySpawnReason.valueOf(compound.getString(key));
            } catch (Exception exception) {
                compound.remove(key);
            }
        }
    }
}
