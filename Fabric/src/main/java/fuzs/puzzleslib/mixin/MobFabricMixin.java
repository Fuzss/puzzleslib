package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.impl.event.SpawnDataMob;
import net.minecraft.nbt.CompoundTag;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobFabricMixin extends LivingEntity implements SpawnDataMob {
    @Shadow
    @Nullable
    private LivingEntity target;
    @Unique
    @Nullable
    private MobSpawnType puzzleslib$spawnType;

    protected MobFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> callback) {
        this.puzzleslib$spawnType = reason;
    }

    @Override
    @Nullable
    public final MobSpawnType puzzleslib$getSpawnType() {
        return this.puzzleslib$spawnType;
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), ordinal = 0)
    public LivingEntity setTarget(@Nullable LivingEntity entity) {
        DefaultedValue<LivingEntity> target = DefaultedValue.fromValue(entity);
        EventResult result = FabricLivingEvents.LIVING_CHANGE_TARGET.invoker().onLivingChangeTarget(this, target);
        return result.isInterrupt() ? this.target : target.getAsOptional().orElse(entity);
    }
}
