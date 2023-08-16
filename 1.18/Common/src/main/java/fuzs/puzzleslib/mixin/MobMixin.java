package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.event.SpawnTypeMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobMixin extends LivingEntity implements SpawnTypeMob {
    @Unique
    @Nullable
    private MobSpawnType puzzleslib$spawnType;

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        if (this.puzzleslib$spawnType != null) {
            String key = PuzzlesLib.id("spawn_type").toString();
            compound.putString(key, this.puzzleslib$spawnType.name());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo callback) {
        String key = PuzzlesLib.id("spawn_type").toString();
        if (compound.contains(key)) {
            try {
                this.puzzleslib$spawnType = MobSpawnType.valueOf(compound.getString(key));
            } catch (Exception ex) {
                compound.remove(key);
            }
        }
    }
}
