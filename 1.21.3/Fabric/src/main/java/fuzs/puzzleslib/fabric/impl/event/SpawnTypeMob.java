package fuzs.puzzleslib.fabric.impl.event;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * An extension to {@link net.minecraft.world.entity.Mob} that stores the {@link EntitySpawnReason} the mob was
 * initially spawned with when
 * {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason,
 * SpawnGroupData)} was called.
 * <p>
 * The implementation is just like Forge, but also allows for setting the spawn type in case e.g.
 * {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason,
 * SpawnGroupData)} was overridden without calling super.
 */
public interface SpawnTypeMob {

    @Nullable
    EntitySpawnReason puzzleslib$getSpawnType();

    void puzzleslib$setSpawnType(@Nullable EntitySpawnReason mobSpawnType);
}
