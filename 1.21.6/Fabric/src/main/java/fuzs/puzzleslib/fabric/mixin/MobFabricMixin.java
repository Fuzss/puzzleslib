package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.util.v1.CodecExtras;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.impl.event.SpawnReasonMob;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.event.data.DefaultedValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity implements SpawnReasonMob {
    @Shadow
    @Nullable
    private LivingEntity target;
    @Unique
    @Nullable
    private EntitySpawnReason puzzleslib$spawnReason;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason, @Nullable SpawnGroupData spawnData, CallbackInfoReturnable<SpawnGroupData> callback) {
        this.puzzleslib$spawnReason = reason;
    }

    @Override
    @Nullable
    public final EntitySpawnReason puzzleslib$getSpawnReason() {
        return this.puzzleslib$spawnReason;
    }

    @Override
    public void puzzleslib$setSpawnReason(@Nullable EntitySpawnReason entitySpawnReason) {
        this.puzzleslib$spawnReason = entitySpawnReason;
    }

    @Inject(method = "setTarget", at = @At("HEAD"))
    public void setTarget(@Nullable LivingEntity livingEntity, CallbackInfo callback) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(livingEntity);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, target);
        if (result.isInterrupt()) {
            callback.cancel();
        } else if (target.getAsOptional().isPresent()) {
            this.target = target.get();
            callback.cancel();
        }
    }

    @Inject(
            method = "checkDespawn", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"
    ), cancellable = true
    )
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo callback) {
        valueOutput.storeNullable(PuzzlesLibMod.id("spawn_type").toString(),
                CodecExtras.ENTITY_SPAWN_REASON_CODEC,
                this.puzzleslib$spawnReason);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(ValueInput valueInput, CallbackInfo callback) {
        this.puzzleslib$spawnReason = valueInput.read(PuzzlesLibMod.id("spawn_type").toString(),
                CodecExtras.ENTITY_SPAWN_REASON_CODEC).orElse(null);
    }
}
